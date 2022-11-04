package com.merchen.gulimall.product.feign;

import com.merchen.common.to.SpuBoundsTO;
import com.merchen.common.to.SkuReductionTO;
import com.merchen.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author MrChen
 * @create 2022-06-24 20:51
 */
@Component
@FeignClient("gulimall-coupon")
public interface CouponFeignService {

    @PostMapping("/coupon/spubounds/save")
    public R saveSpuBounds(@RequestBody  SpuBoundsTO spuBoundsTO);

    @PostMapping("/coupon/skufullreduction/saveInfo")
    public R saveReductionInfo(@RequestBody SkuReductionTO skuReductionTO);
}
