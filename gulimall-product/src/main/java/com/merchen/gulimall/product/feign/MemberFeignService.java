package com.merchen.gulimall.product.feign;

import com.merchen.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author MrChen
 * @create 2022-08-23 19:23
 */
@Component
@FeignClient("gulimall-member")
public interface MemberFeignService {

    @RequestMapping("/member/member/info/{id}")
    public R infoRemote(@PathVariable("id") Long id);

}
