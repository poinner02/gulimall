package com.merchen.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author MrChen
 * @create 2022-06-24 21:06
 */
@Data
public class SpuBoundsTO {

    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
