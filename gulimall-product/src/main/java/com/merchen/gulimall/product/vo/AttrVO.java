package com.merchen.gulimall.product.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品属性
 *
 * @author ChenZhongBo
 * @email 961208477@qq.com
 * @date 2022-06-04 20:05:18
 */

@Data
public class AttrVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * "attrId": 7,
     * 			"attrName": "入网型号",
     * 			"searchType": 1,
     * 			"valueType": 0,
     * 			"icon": "xxx",
     * 			"valueSelect": "aaa;bb",
     * 			"attrType": 1,
     * 			"enable": 1,
     * 			"catelogId": 225,
     * 			"showDesc": 1,
     * 			"attrGroupId": null
     *
     */

    /**
     * 属性id
     */
    private Long attrId;
    /**
     * 属性名
     */
    private String attrName;
    /**
     * 是否需要检索[0-不需要，1-需要]
     */
    private Integer searchType;
    /**
     * 值类型[0-为单个值，1-可以选择多个值]
     */
    private Integer valueType;
    /**
     * 属性图标
     */
    private String icon;
    /**
     * 可选值列表[用逗号分隔]
     */
    private String valueSelect;
    /**
     * 属性类型[0-销售属性，1-基本属性，2-既是销售属性又是基本属性]
     */
    private Integer attrType;
    /**
     * 启用状态[0 - 禁用，1 - 启用]
     */
    private Long enable;
    /**
     * 所属分类
     */
    private Long catelogId;
    /**
     * 快速展示【是否展示在介绍上；0-否 1-是】，在sku中仍然可以调整
     */
    private Integer showDesc;

    private Long [] catelogPath;

    private Long attrGroupId;

}
