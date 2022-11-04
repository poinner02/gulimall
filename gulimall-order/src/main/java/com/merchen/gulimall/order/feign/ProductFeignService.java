package com.merchen.gulimall.order.feign;

import com.merchen.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author MrChen
 * @create 2022-09-10 20:24
 */
@Component
@FeignClient("gulimall-product")
public interface ProductFeignService {

    @RequestMapping("product/skuinfo/info/{skuId}")
    public R skuInfo(@PathVariable("skuId") Long skuId);

    @RequestMapping("product/spuinfo/info/{id}")
    public R spuInfo(@PathVariable("id") Long id);

    @RequestMapping("product/brand/info/{brandId}")
    public R info(@PathVariable("brandId") Long brandId);
}
