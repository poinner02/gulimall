package com.merchen.gulimall.ware.feign;

import com.merchen.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author MrChen
 * @create 2022-06-29 19:21
 */
@Component
@FeignClient("gulimall-product")
public interface ProductFeignService {

    @RequestMapping("product/skuinfo/remote/info/{skuId}")
    public R RemoteInfo(@PathVariable("skuId") Long skuId);


}
