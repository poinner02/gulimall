package com.merchen.gulimall.search.vo;


import com.merchen.common.to.elasticSearch.EsModel;
import lombok.Data;

import java.util.List;

/**
 * @author MrChen
 * @create 2022-08-04 19:07
 */
@Data
public class SearchResult {
    /**
     * 全文检索的商品信息
     */
    private List<EsModel> products;// 检索的所有商品信息
    /**
     * 分页信息
     */
    private Integer pageNum; //当前页
    private Long totalPageNum; //总页码数
    private List<Long> pageNavs;
    private Long total; //总记录数
    private List<BrandVO> brands;
    private List<CategoryVO> categorys;
    private List<AttrVO> attrVOS;
    //面包屑导航
    private List<NavVo> navs;
    @Data
    public static class NavVo{
        private String navName;
        private String navValue;
        private String link;
    }
    /*= ====================公共状态栏信息=======================*/
    /**
     * 全文检索的品牌
     */
    @Data
    public static class BrandVO {

        private Long brandId;
        private String brandImg;
        private String brandName;
    }
    /**
     * 全文检索的分类
     */
    @Data
    public static class CategoryVO{
        private Long catalogId;
        private String catalogName;
    }

    /**
     * 全文检索的属性
     */
    @Data
    public static class  AttrVO{
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }


}
