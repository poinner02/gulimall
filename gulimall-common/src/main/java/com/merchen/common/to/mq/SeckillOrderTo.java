package com.merchen.common.to.mq;


import lombok.Data;

import java.math.BigDecimal;

/**
 * @author MrChen
 * @create 2022-10-13 22:39
 */
@Data
public class SeckillOrderTo {
    private String orderSn;//秒杀订单号
    private Long skuId; //商品Id
    private Integer num; //购买数量
    private Long memberId; //会员Id
    private BigDecimal secKillPrice; //秒杀价格
    private Long promotionSessionId; //活动场次Id




}
