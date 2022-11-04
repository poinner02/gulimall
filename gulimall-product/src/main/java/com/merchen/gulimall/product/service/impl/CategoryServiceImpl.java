package com.merchen.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.merchen.gulimall.product.service.CategoryBrandRelationService;
import com.merchen.gulimall.product.vo.CateLogory2VO;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.merchen.common.utils.PageUtils;
import com.merchen.common.utils.Query;

import com.merchen.gulimall.product.dao.CategoryDao;
import com.merchen.gulimall.product.entity.CategoryEntity;
import com.merchen.gulimall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    private RedissonClient redisson;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //查询所有分类
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);
        //组装成父子树形结构
        List<CategoryEntity> level1Menus = categoryEntities.stream().filter((categoryEntity) -> {
            //找到1级分类
            //这里解决BUG问题,当使用Long对象时，-127~127可以直接用比较大小符号，当超过后用equals比较
            return categoryEntity.getParentCid().equals(new Long("0"));
            //映射
        }).map((menu) -> {
            menu.setChildren(getChildrens(menu, categoryEntities));
            return menu;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());

        return level1Menus;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO 1.检查当前删除菜单，是否被其他地方引用
        baseMapper.deleteBatchIds(asList);
    }

    @Override
    public Long[] getCategoryPath(Long catelogId) {
        List<Long> path = new ArrayList<>();
        List<Long> categoryPath = findCategoryPath(catelogId, path);
        return categoryPath.toArray(new Long[categoryPath.size()]);
    }

    //删除缓存，失效模式
    @CacheEvict(value = "category",allEntries = true)
    @Transactional
    @Override
     public void updateDetails(CategoryEntity category) {

        baseMapper.updateById(category);
        //更新关联关系 todo
        //更新关联关系categoryBrandRelation
        categoryBrandRelationService.updateCateGoryDetails(category.getCatId(), category.getName());

    }

    @Cacheable(value = {"category"},key = "#root.methodName")
    @Override
    public List<CategoryEntity> getOneLeveL() {
        QueryWrapper<CategoryEntity> wrapper = new QueryWrapper<>();
        Integer catLevel = 1; // 默认是1级分类查询
        wrapper.eq("cat_level", catLevel);
//        System.out.println(System.currentTimeMillis());
        return this.baseMapper.selectList(wrapper);
    }

    //使用cacheable注解生效缓存,开启分布式锁
//    @Cacheable(value = "category",key = "'cateGoryJson'",sync = true)
//    @Override
    public Map<String, List<CateLogory2VO>> getcateGoryJson1(){
        Map<String, List<CateLogory2VO>> stringListMap = getcateGoryJsonFromDbByRedissonLock();
        return stringListMap;
    }



    //缓存优化
    @Override
    public Map<String, List<CateLogory2VO>> getcateGoryJson() {
        //解决缓存穿透:大量并发访问一个不存在的数据，解决：对查询的数据为null也储存到redis中
        //缓存雪崩:大量并发同时发访问多个数据，多个数据并且都过期了，解决：对放入redis中的数据设置过期时间
        //缓存击穿:大量并发访问某个热点数据，且这个热点数据正好过期了，解决：对访问这个数据的服务或者业务加锁lock
        Map<String, List<CateLogory2VO>> stringListMap = null;
        //1.去缓存中找数据
        String categoryJson = stringRedisTemplate.opsForValue().get("categoryJson");
        if (StringUtils.isEmpty(categoryJson)) {
            System.out.println("缓存没有命中.............");
            //2.没有则去db查 if(true)
            //解决缓存穿透:大量并发访问一个不存在的数据，解决：对查询的数据为null也储存到redis中
            //缓存雪崩:大量并发同时发访问多个数据，多个数据并且都过期了，解决：对放入redis中的数据设置过期时间
            //stringListMap 为null也储存了
            stringListMap = getcateGoryJsonFromDbByRedissonLock();
        } else {
            System.out.println("缓存命中.............");
            //json字符串逆转成对象，{序列化和反序列化}
            stringListMap = JSON.parseObject(categoryJson, new TypeReference<Map<String, List<CateLogory2VO>>>() {
            });
        }
        return stringListMap;

    }

    //分布式锁
    public Map<String, List<CateLogory2VO>> getcateGoryJsonFromDbByRedissonLock() {

        RLock lock = redisson.getLock("cateGoryJson-lock");
        lock.lock();//阻塞试等待
        Map<String, List<CateLogory2VO>> stringListMap = null;
        try {
            stringListMap = getStringListMap();
        } finally {
            lock.unlock();
            return stringListMap;
        }
    }

    //分布式锁,解决本地和多台机器多线程服务的大并发缓存击穿问题
    @Deprecated
    public Map<String, List<CateLogory2VO>> getcateGoryJsonFromDbByRedLock() {

        String uuid = UUID.randomUUID().toString();
        //redis set lock uuid ex 30 nx      return true or false  加锁原子性
        Boolean lock1 = stringRedisTemplate.opsForValue().setIfAbsent("lock", uuid, 30, TimeUnit.SECONDS);

        if (lock1) {
            System.out.println("抢占分布式锁成功。。。。。。");
            //业务
            Map<String, List<CateLogory2VO>> stringListMap = null;
            //抢锁成功
            try {
                stringListMap = getStringListMap();
            } finally {
                //lua脚本 释放分布式锁  解锁原子性
                String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
                Long lock = stringRedisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList("lock"), uuid);
                return stringListMap;
            }
        } else {

            //todo设置睡眠时间
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            //自旋抢锁
            return getcateGoryJsonFromDbByRedLock();
        }
    }

    //本地锁,解决本地当前服务的大并发缓存击穿问题
    @Deprecated
    public Map<String, List<CateLogory2VO>> getcateGoryJsonFromDbByLocalSynchronized() {
        synchronized (this) {
            return getStringListMap();
        }
    }

    public Map<String, List<CateLogory2VO>> getStringListMap1() {
        String categoryJson = stringRedisTemplate.opsForValue().get("category::cateGoryJson");
        //缓存中有数据直接反序列化成对象
        if (!StringUtils.isEmpty(categoryJson)) {
            //json字符串逆转成对象，{序列化和反序列化}
            Map<String, List<CateLogory2VO>> stringListMap = JSON.parseObject(categoryJson, new TypeReference<Map<String, List<CateLogory2VO>>>() {
            });
            return stringListMap;
        }
        //缓存中没有数据
        //查询所有的品牌
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);
        //查询1级分类
        List<CategoryEntity> oneLeveL = getOneLeveL();
        //收集成map
        Map<String, List<CateLogory2VO>> parent_cid = oneLeveL.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), l1 -> {
            //查询每个1级 分类下的 2级分类
            List<CategoryEntity> category1VOs = getParentCid(categoryEntities, l1.getCatId());
            List<CateLogory2VO> collect = null;
            if (category1VOs != null) {
                collect = category1VOs.stream().map(l2 -> {
                    List<CategoryEntity> category2VOs = getParentCid(categoryEntities, l2.getCatId());
                    //查询每个2级 分类下的3级分类
                    List<CateLogory2VO.catalog3VO> catalog3VOS = null;
                    if (category2VOs != null) {
                        catalog3VOS = category2VOs.stream().map(l3 -> {
                            CateLogory2VO.catalog3VO catalog3VO = new CateLogory2VO.catalog3VO(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            return catalog3VO;
                        }).collect(Collectors.toList());
                    }
                    CateLogory2VO cateLogory2VO = new CateLogory2VO(l1.getCatId().toString(), catalog3VOS, l2.getCatId().toString(), l2.getName());
                    return cateLogory2VO;
                }).collect(Collectors.toList());
            }
            return collect;
        }));
        System.out.println("查询数据库...");
        //3.json存入缓存是为了解耦
        stringRedisTemplate.opsForValue().set("category::cateGoryJson", JSON.toJSONString(parent_cid), 1, TimeUnit.HOURS);
        return parent_cid;
    }

    //做了一遍缓存查询
    private Map<String, List<CateLogory2VO>> getStringListMap() {
        String categoryJson = stringRedisTemplate.opsForValue().get("categoryJson");
        //缓存中有数据直接反序列化成对象
        if (!StringUtils.isEmpty(categoryJson)) {
            //json字符串逆转成对象，{序列化和反序列化}
            Map<String, List<CateLogory2VO>> stringListMap = JSON.parseObject(categoryJson, new TypeReference<Map<String, List<CateLogory2VO>>>() {
            });
            return stringListMap;
        }
        //缓存中没有数据
        //查询所有的品牌
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);
        //查询1级分类
        List<CategoryEntity> oneLeveL = getOneLeveL();
        //收集成map
        Map<String, List<CateLogory2VO>> parent_cid = oneLeveL.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), l1 -> {
            //查询每个1级 分类下的 2级分类
            List<CategoryEntity> category1VOs = getParentCid(categoryEntities, l1.getCatId());
            List<CateLogory2VO> collect = null;
            if (category1VOs != null) {
                collect = category1VOs.stream().map(l2 -> {
                    List<CategoryEntity> category2VOs = getParentCid(categoryEntities, l2.getCatId());
                    //查询每个2级 分类下的3级分类
                    List<CateLogory2VO.catalog3VO> catalog3VOS = null;
                    if (category2VOs != null) {
                        catalog3VOS = category2VOs.stream().map(l3 -> {
                            CateLogory2VO.catalog3VO catalog3VO = new CateLogory2VO.catalog3VO(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            return catalog3VO;
                        }).collect(Collectors.toList());
                    }
                    CateLogory2VO cateLogory2VO = new CateLogory2VO(l1.getCatId().toString(), catalog3VOS, l2.getCatId().toString(), l2.getName());
                    return cateLogory2VO;
                }).collect(Collectors.toList());
            }
            return collect;
        }));
        System.out.println("查询数据库...");
        //3.json存入缓存是为了解耦
        stringRedisTemplate.opsForValue().set("categoryJson", JSON.toJSONString(parent_cid), 1, TimeUnit.DAYS);
        return parent_cid;
    }

    private List<CategoryEntity> getParentCid(List<CategoryEntity> categoryEntities, Long parentCid) {
//        return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", l1.getCatId()));
        List<CategoryEntity> collect = categoryEntities.stream().filter(item -> {
            //过滤parentid为指定的
            return item.getParentCid().equals(parentCid);
        }).collect(Collectors.toList());
        return collect;
    }

    private List<Long> findCategoryPath(Long catelogId, List<Long> path) {

        path.add(catelogId);
        CategoryEntity categoryEntity = baseMapper.selectById(catelogId);

        if (!categoryEntity.getParentCid().equals(0L)) {
            findCategoryPath(categoryEntity.getParentCid(), path);
        }
        return path;

    }


    private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all) {

        List<CategoryEntity> childrenList = all.stream().filter((categoryEntity) -> {
            //建立映射关系
            return categoryEntity.getParentCid().equals(root.getCatId());
        }).map((categoryEntity) -> {
            //找到子菜单
            categoryEntity.setChildren(getChildrens(categoryEntity, all));
            return categoryEntity;
            //排序
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());
        return childrenList;
    }

}