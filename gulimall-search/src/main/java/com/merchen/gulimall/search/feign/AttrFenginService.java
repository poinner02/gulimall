package com.merchen.gulimall.search.feign;

import com.merchen.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author MrChen
 * @create 2022-08-09 20:25
 */

@FeignClient("gulimall-product")
public interface AttrFenginService {
    @GetMapping("product/attr/info/{attrId}")
    public R info(@PathVariable("attrId") Long attrId);
}
