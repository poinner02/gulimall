package com.merchen.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author MrChen
 * @create 2022-06-24 21:52
 */
@Data
public class SkuReductionTO {
    private Long skuId;
    private int fullCount;
    private BigDecimal discount;
    private int countStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    private List<MemberPrice> memberPrice;

}
