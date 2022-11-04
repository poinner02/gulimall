package com.merchen.gulimall.order.feign;

import com.merchen.common.utils.R;
import com.merchen.gulimall.order.vo.FareVo;
import com.merchen.gulimall.order.vo.WareSkuLockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author MrChen
 * @create 2022-09-06 20:30
 */
@Component
@FeignClient("gulimall-ware")
public interface WareFeignService {

    @PostMapping("ware/waresku/hasStock")
    public R getSkuHasStock(@RequestBody List<Long> skuIds);

    @GetMapping("ware/wareinfo/fare")
    public FareVo getFare(@RequestParam("attrId")Long attrId);

    @PostMapping("ware/waresku/orderLockStock")
    public R orderLockStock(@RequestBody WareSkuLockVo wareSkuLockVo);
}
