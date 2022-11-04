package com.merchenl.gulimall.cartservice.feignService;

import com.merchen.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author MrChen
 * @create 2022-08-26 19:51
 */
@Component
@FeignClient("gulimall-product")
public interface ProductFeignService {

    @RequestMapping("product/skuinfo/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId);

    @GetMapping("product/skusaleattrvalue/get/sku/attr/values/{skuId}")
    public List<String> getSkuAttrValues(@PathVariable("skuId")Long skuId);

}
