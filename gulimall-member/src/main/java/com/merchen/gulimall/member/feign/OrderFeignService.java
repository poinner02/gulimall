package com.merchen.gulimall.member.feign;

import com.merchen.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * @author MrChen
 * @create 2022-09-26 22:29
 */
@Component
@FeignClient("gulimall-order")
public interface OrderFeignService {
    @PostMapping("/order/order/listWitchOrderItem")
    public R listWitchOrderItem(@RequestBody Map<String, Object> params);
}
