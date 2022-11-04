package com.merchen.gulimall.search.vo;

import lombok.Data;

import java.util.List;

/**
 * @author MrChen
 * @create 2022-08-03 21:20
 * 谷粒商城检索条件封装
 */
@Data
public class SearchParm {
    /**
     * 全文检索条件
     */
    private String  keyword ;
    /**
     *  3级分类id
     */
    private Long catalog3Id;

    /**
     * 是否有货 0无1有
     */
    private Integer hasStock;
    /**
     * 各种sort排序
     * 销量、价格、评分、上架时间
     * 格式:
     *  sort_saleCount,sort_price,sort_commitCount,sort_saled
     */
    private String sort;

    /**
     * 价格区间过滤
     * 格式：
     */
    private String skuPrice;

    /**
     * 品牌分类id，可以多选
     */
    private List<Long>  brandIds;

    /**
     * 属性
     *  _500,1_500,500_
     * 格式：
     *  attrs=[1_5寸:6寸,2_黑色:白色]
     *
     */
    private List<String> attrs;

    //分页
    private Integer pageNumber = 1;

    private String _queryString;
}
