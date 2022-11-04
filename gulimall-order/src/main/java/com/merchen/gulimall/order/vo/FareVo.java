package com.merchen.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单确认页面，收获人信息和运费
 * @author MrChen
 * @create 2022-09-07 23:10
 */
@Data
public class FareVo {
    /**
     * 运费
     */
    BigDecimal fare;
    /**
     * 收货人信息
     */
    MemberReceiveAddressEntity memberReceiveAddressEntity;
}
