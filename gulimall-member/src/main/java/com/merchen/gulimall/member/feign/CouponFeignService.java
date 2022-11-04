package com.merchen.gulimall.member.feign;

import com.merchen.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @author MrChen
 * @create 2022-06-05 20:51
 */
@FeignClient("gulimall-coupon")
@Component
public interface CouponFeignService {


    //todo
    @RequestMapping("coupon/coupon/remote/list")
    public R list();

}
