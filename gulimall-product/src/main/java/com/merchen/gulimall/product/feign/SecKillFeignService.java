package com.merchen.gulimall.product.feign;

import com.merchen.common.utils.R;
import com.merchen.gulimall.product.exception.ErrorException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author MrChen
 * @create 2022-10-10 21:52
 */
@Component
@FeignClient(value = "secskill-server",fallback = ErrorException.class)
public interface SecKillFeignService {
    /**
     * 获取某个秒杀商品
     * @param skuId
     * @return
     */
    @GetMapping("/get/seckill/{skuId}")
    public R getSecKillSkuInfo(@PathVariable("skuId")Long skuId);
}
