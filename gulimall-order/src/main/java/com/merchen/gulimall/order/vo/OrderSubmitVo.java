package com.merchen.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author MrChen
 * @create 2022-09-09 21:07
 */
@Data
public class OrderSubmitVo {
    /**
     * 收货人地址id
     */
    private Long addrId;
    /**
     * 支付方式
     */
    private Integer payeType;
    /**
     * 应付金额 ，检验
     */
    private BigDecimal payePrice;
    /**
     * 订单令牌
     */
    private String token;
    //用户信息。直接去session中取
}
