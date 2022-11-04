package com.merchen.gulimall.product;




import com.alibaba.fastjson.JSON;
import com.merchen.gulimall.product.dao.AttrGroupDao;
import com.merchen.gulimall.product.dao.SkuSaleAttrValueDao;
import com.merchen.gulimall.product.service.BrandService;
import com.merchen.gulimall.product.service.CategoryService;
import com.merchen.gulimall.product.vo.SkuItemVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;


/**
 * @author MrChen
 * @create 2022-06-04 21:13
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class GuliMallApplicationTest {


    @Autowired
    private BrandService brandService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedissonClient redisson;

    @Autowired
    AttrGroupDao attrGroupDao;

    @Autowired
    SkuSaleAttrValueDao skuSaleAttrValueDao;

    @Test
    public  void skuSale(){
        List<SkuItemVo.ItemSaleItemAttrsVo> saleAttrsBySpuId = skuSaleAttrValueDao.getSaleAttrsBySpuId(2L);
        String s = JSON.toJSONString(saleAttrsBySpuId);
        System.out.println(s);
    }


    @Test
    public  void testItem(){
        List<SkuItemVo.SpuItemAttrGroupVo> group = attrGroupDao.getAttrGroupWithAttrsBySpuIdAndCateGoryId(2L, 225L);
        System.out.println(group);
    }

    @Test
    public void test(){
        Long[] categoryPath = categoryService.getCategoryPath(225L);
        log.info("完整路径：{}", Arrays.asList(categoryPath));
    }

    @Test
    public void test1(){
        Integer append = stringRedisTemplate.opsForValue().append("k1", "hello world");
        log.info("redis append：{}",append);
    }

    @Test
    public void RedissionTest(){

        RMap map = redisson.getMap("categoryJson");
        String name = map.getName();// = mymap
        System.out.println(name);

    }
}
