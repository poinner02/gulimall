package com.merchen.gulimall.order.to;

import com.merchen.gulimall.order.entity.OrderEntity;
import com.merchen.gulimall.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author MrChen
 * @create 2022-09-10 19:41
 */
@Data
public class OrderCreateTo {
    private OrderEntity order;
    private List<OrderItemEntity> orderItemList;
    private BigDecimal payPrice;
    private BigDecimal fare;

}
