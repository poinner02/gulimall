package com.merchem.gulimall.secskill.feign;

import com.merchem.gulimall.secskill.feign.fallBack.SecKillFeignServiceFallBack;
import com.merchem.gulimall.secskill.vo.SkuInfoVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author MrChen
 * @create 2022-10-07 22:14
 */
@Component
@FeignClient(value = "gulimall-product",fallback = SecKillFeignServiceFallBack.class)
public interface ProductFeignService {
    @RequestMapping("/product/skuinfo/seckill/remote/info/{skuId}")
    public SkuInfoVo seckillRemoteInfo(@PathVariable("skuId") Long skuId);

}
