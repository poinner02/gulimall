package com.merchen.gulimall.ware.vo;


import lombok.Data;

import java.util.List;

/**
 * 订单锁库存vo
 * @author MrChen
 * @create 2022-09-11 22:07
 */
@Data
public class WareSkuLockVo {
    private String orderSn;//订单号
    private List<OrderItemEntity> locks;//所有的库存信息
}
