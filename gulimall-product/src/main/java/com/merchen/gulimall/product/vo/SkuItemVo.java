package com.merchen.gulimall.product.vo;

import com.merchen.gulimall.product.entity.SkuImagesEntity;
import com.merchen.gulimall.product.entity.SkuInfoEntity;
import com.merchen.gulimall.product.entity.SpuInfoDescEntity;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author MrChen
 * @create 2022-08-11 21:06
 */
@ToString
@Data
public class SkuItemVo {
    //1、sku 基本信息  pms_sku_info
    private SkuInfoEntity info;
    //2、sku 图片信息  pms_sku_images
    private List<SkuImagesEntity> images;
    //3、spu 销售属性组合
    private List<ItemSaleItemAttrsVo> saleAttrs;
    //4、spu 基本介绍 pms_spu_info_desc
    private SpuInfoDescEntity desc;
    //5、spu 规格参数信息
    private List<SpuItemAttrGroupVo> goupAttrs;
    //6、sku 库存信息 true 有 false 没有
    private Boolean has_stock = Boolean.FALSE;

    private SecKillSkuInfo secKillSkuInfo;

    @Data
    public static class ItemSaleItemAttrsVo {
        private Long attrId;
        private String attrName;
        private List<AttrValueWithSkuIdVo> attrValues;

    }
    @ToString
    @Data
    public static  class SpuItemAttrGroupVo {
        private String groupName;
        private List<SpuBaseAttrVo> attrs;

    }
    @ToString
    @Data
    public static class SpuBaseAttrVo {
        private String attrName;
        private String attrValue;
    }

}
