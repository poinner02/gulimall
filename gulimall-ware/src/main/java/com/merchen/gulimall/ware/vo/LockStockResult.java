package com.merchen.gulimall.ware.vo;

import lombok.Data;

/**
 * @author MrChen
 * @create 2022-09-11 22:23
 */
@Data
public class LockStockResult {
    private Long skuId;
    private Integer num;
    private Boolean locked;
}
