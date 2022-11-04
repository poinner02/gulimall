package com.merchem.gulimall.secskill.feign;

import com.merchen.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author MrChen
 * @create 2022-09-30 23:57
 */
@Component
@FeignClient("gulimall-coupon")
public interface CouponFeignService {

    @GetMapping("/coupon/seckillsession/getseckill/sessionrecenty3day")
    public R getSeckillSessionRecenty3Day();
}
