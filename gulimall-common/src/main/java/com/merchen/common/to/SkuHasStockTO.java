package com.merchen.common.to;

import lombok.Data;

/**
 * @author MrChen
 * @create 2022-07-11 11:55
 */
@Data
public class SkuHasStockTO {
    private Long skuId;
    private Integer stock;
}
