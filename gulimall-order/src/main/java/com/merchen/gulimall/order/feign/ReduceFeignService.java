package com.merchen.gulimall.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

/**
 * @author MrChen
 * @create 2022-09-02 19:35
 */
@Component
@FeignClient("gulimall-reduce-service")
public interface ReduceFeignService {
    //todo
}
