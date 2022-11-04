package com.merchen.common.to.elasticSearch;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author MrChen
 * @create 2022-07-10 11:44
 */
@Data
public class EsModel {

    private Long skuId;
    private Long spuId;
    private Long brandId;
    private String skuImg;
    private BigDecimal skuPrice;
    private String skuTitle;
    private String brandImg;
    private String brandName;
    private Long catalogId;
    private String catalogName;
    private Boolean hasStock;
    private Long hotScore;
    private Long saleCount;
    private List<Attr> attrs;

    @Data
    public static class Attr{
        private Long attrId;
        private String attrName;
        private String attrValue;
    }
}
