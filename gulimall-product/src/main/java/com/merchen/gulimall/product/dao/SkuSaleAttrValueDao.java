package com.merchen.gulimall.product.dao;

import com.merchen.gulimall.product.entity.SkuSaleAttrValueEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.merchen.gulimall.product.vo.SkuItemVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * sku销售属性&值
 * 
 * @author ChenZhongBo
 * @email 961208477@qq.com
 * @date 2022-06-04 20:05:18
 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {

    List<SkuItemVo.ItemSaleItemAttrsVo> getSaleAttrsBySpuId(Long spuId);

    List<String> getCartAttrs(Long skuId);
}
