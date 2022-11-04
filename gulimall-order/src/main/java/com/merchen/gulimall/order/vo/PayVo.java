package com.merchen.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author MrChen
 * @create 2022-09-25 22:25
 */
@Data
public class PayVo {
    private String out_trade_no; //订单号
    private BigDecimal total_amount; //订单金额
    private String subject; //名称
    private String body; //商品描述
}
