package com.merchen.gulimall.product.service.impl;

import com.merchen.common.constant.ProductConstant;
import com.merchen.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.merchen.gulimall.product.dao.AttrDao;
import com.merchen.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.merchen.gulimall.product.entity.AttrEntity;
import com.merchen.gulimall.product.service.AttrService;
import com.merchen.gulimall.product.vo.AttrGruopVO;
import com.merchen.gulimall.product.vo.SkuItemVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.merchen.common.utils.PageUtils;
import com.merchen.common.utils.Query;

import com.merchen.gulimall.product.dao.AttrGroupDao;
import com.merchen.gulimall.product.entity.AttrGroupEntity;
import com.merchen.gulimall.product.service.AttrGroupService;
import org.springframework.util.StringUtils;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private AttrAttrgroupRelationDao attrAttrgroupRelationDao;

    @Autowired
    private AttrDao attrDao;

    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private AttrService attrService;

    @Override
    public List<AttrEntity> getReationDetails(Long attrGroupId) {

        //查inner表
        QueryWrapper<AttrAttrgroupRelationEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("attr_group_id", attrGroupId);
        //查找attrid集合
        List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntities = attrAttrgroupRelationDao.selectList(wrapper);
        List<Long> attrIds = attrAttrgroupRelationEntities.stream().map((item -> {
            return item.getAttrId();
        })).collect(Collectors.toList());
        //查attr表
        List<AttrEntity> attrEntities = new ArrayList<>();
        if (attrIds.size() > 0) {
            attrEntities = attrDao.selectBatchIds(attrIds);
        }
        return attrEntities;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {

        IPage<AttrGroupEntity> ipage = new Query<AttrGroupEntity>().getPage(params);
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<>();

        //模糊查询
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            //select * from pms_attr_group where catelog_id = ? and ( catelogId = ? or attr_group_name like %?%)
            wrapper.and((object) -> {
                object.eq("attr_group_id", key).or().like("attr_group_name", key);
            });
        }

        if (catelogId.equals(0L)) {
            //查询全部
            IPage<AttrGroupEntity> page = this.page(ipage, wrapper);
            return new PageUtils(page);
        } else {
            //单独查询
            wrapper.eq("catelog_id", catelogId);
            IPage<AttrGroupEntity> page = this.page(ipage, wrapper);
            return new PageUtils(page);
        }
    }

    //todo 查询非关联的属性
    @Override
    public PageUtils getNoAttrAndAttrGroupRelation(Long attrGroupId, Map<String, Object> params) {
        //封装page
        IPage<AttrEntity> ipage = new Query<AttrEntity>().getPage(params);
        //查询条件
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            //模糊查询
            wrapper.and((object) -> {
                object.like("attr_name", key).or().eq("attr_id", key);
            });
        }
        //获取当前分组的分类id
        AttrGroupEntity attrGroupEntity = attrGroupService.getById(attrGroupId);
        Long catelogId = attrGroupEntity.getCatelogId();
        //查询当前分组的当前分类下的规格属性  sql：select * from xx where catelog_id = ? and attr_type = ?
        wrapper.eq("catelog_id", catelogId).eq("attr_type", ProductConstant.AttrEnum.ATTR_TYPE_BAES.getCode());
        //查询当前规格属性分类下的分页数据Attr
        IPage<AttrEntity> attrEntityIPage = attrDao.selectPage(ipage, wrapper);
        //获取attrlist
        List<AttrEntity> attrEntities = attrEntityIPage.getRecords();
        //查询已经关联好的属性
        QueryWrapper<AttrAttrgroupRelationEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("attr_group_id", attrGroupId);
        List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntities = attrAttrgroupRelationDao.selectList(queryWrapper);
        List<Long> ids = attrAttrgroupRelationEntities.stream().map((item) -> {
            return item.getAttrId();
        }).collect(Collectors.toList());
        //筛选未过滤的attr,并且收集
        List<AttrEntity> collect = attrEntities.stream().filter((item) -> {
            return !ids.contains(item.getAttrId());
        }).collect(Collectors.toList());

        attrEntityIPage.setRecords(collect);
        return new PageUtils(attrEntityIPage);
    }

    @Override
    public List<AttrGruopVO> getAtttrGroupList(Long cateId) {

        //查询分组
        List<AttrGroupEntity> attrGroupEntities = attrGroupService.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", cateId));
        List<AttrGruopVO> attrGruopVOS = attrGroupEntities.stream().map((item) -> {
            AttrGruopVO attrGruopVO = new AttrGruopVO();
            BeanUtils.copyProperties(item, attrGruopVO);
            //根据分组id获取attr
            List<AttrEntity> attrList = attrService.getRelationAttr(item.getAttrGroupId());
            attrGruopVO.setAttrs(attrList);
            return attrGruopVO;
        }).collect(Collectors.toList());
        return attrGruopVOS;
    }

    @Override
    public List<SkuItemVo.SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuIdAndCateGoryId(Long spuId,Long categoryId){
        List<SkuItemVo.SpuItemAttrGroupVo> spuItemAttrGroupVos = baseMapper.getAttrGroupWithAttrsBySpuIdAndCateGoryId(spuId,categoryId);
        return spuItemAttrGroupVos;
    }


}