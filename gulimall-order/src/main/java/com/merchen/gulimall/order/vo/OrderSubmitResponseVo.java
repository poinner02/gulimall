package com.merchen.gulimall.order.vo;

import com.merchen.gulimall.order.entity.OrderEntity;
import lombok.Data;

/**
 * @author MrChen
 * @create 2022-09-10 15:46
 */
@Data
public class OrderSubmitResponseVo {
    private OrderEntity orderEntity;
    private Integer code;//0 成功，1 错误
}
