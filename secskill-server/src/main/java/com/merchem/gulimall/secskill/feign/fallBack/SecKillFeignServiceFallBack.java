package com.merchem.gulimall.secskill.feign.fallBack;

import com.merchem.gulimall.secskill.feign.ProductFeignService;
import com.merchem.gulimall.secskill.vo.SkuInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author MrChen
 * @create 2022-10-27 21:58
 */
@Slf4j
@Component
public class SecKillFeignServiceFallBack implements ProductFeignService {
    @Override
    public SkuInfoVo seckillRemoteInfo(Long skuId) {
        log.info("sentinel熔断机制{}",skuId);
        return null;
    }
}
