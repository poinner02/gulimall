/**
  * Copyright 2022 json.cn 
  */
package com.merchen.gulimall.product.vo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Auto-generated: 2022-06-23 21:32:26
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */
@Data
public class SpuSaveVO {

    /**
     * 商品基本属性
     */
    private String spuName;
    /**
     * 商品描述
     */
    private String spuDescription;
    /**
     * 商品分类
     */
    private Long catalogId;
    /**
     * 商品品牌
     */
    private Long brandId;
    /**
     * 商品重量
     */
    private BigDecimal weight;
    /**
     * 商品是否上线 0 不上， 1上线
     */
    private int publishStatus;
    /**
     * 描述
     */
    private List<String> decript;
    /**
     * 商品图集
     */
    private List<String> images;
    /**
     * 积分
     */
    private Bounds bounds;
    /**
     * 基本属性集合
     */
    private List<BaseAttrs> baseAttrs;
    /**
     * 销售属性集合
     */
    private List<Skus> skus;


}