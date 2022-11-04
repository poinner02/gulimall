package com.merchen.gulimall.ware.feign;

import com.merchen.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author MrChen
 * @create 2022-09-19 22:30
 */
@Component
@FeignClient("gulimall-order")
public interface OrderFeignService {

    @GetMapping("order/order/status/getStatus/{ordersn}")
    public R getStatus(@PathVariable("ordersn")String orderSn);
}
