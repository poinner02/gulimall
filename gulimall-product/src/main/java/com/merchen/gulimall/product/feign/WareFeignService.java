package com.merchen.gulimall.product.feign;

import com.merchen.common.to.SkuHasStockTO;
import com.merchen.common.to.WareSkuTO;
import com.merchen.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author MrChen
 * @create 2022-07-10 12:23
 */
@FeignClient("gulimall-ware")
public interface WareFeignService {


    @PostMapping("/ware/waresku/hasStock")
    public R getSkuHasStock(@RequestBody List<Long> skuIds);
}
