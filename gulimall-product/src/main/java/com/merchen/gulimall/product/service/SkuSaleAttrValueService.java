package com.merchen.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.merchen.common.utils.PageUtils;
import com.merchen.gulimall.product.entity.SkuSaleAttrValueEntity;
import com.merchen.gulimall.product.vo.SkuItemVo;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author ChenZhongBo
 * @email 961208477@qq.com
 * @date 2022-06-04 20:05:18
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<SkuItemVo.ItemSaleItemAttrsVo> getSaleAttrsBySpuId(Long spuId);

    List<String> getCartAttrs(Long skuId);
}

