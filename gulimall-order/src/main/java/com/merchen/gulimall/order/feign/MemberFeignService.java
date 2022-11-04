package com.merchen.gulimall.order.feign;

import com.merchen.common.utils.R;
import com.merchen.gulimall.order.vo.MemberReceiveAddressEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author MrChen
 * @create 2022-08-31 22:27
 */
@Component
@FeignClient("gulimall-member")
public interface MemberFeignService {
    @RequestMapping("member/memberreceiveaddress/save")
    public R save(@RequestBody MemberReceiveAddressEntity memberReceiveAddress);

    @RequestMapping("member/memberreceiveaddress/search/list")
    public R list(@RequestParam("memberId")Long memberId);

    @RequestMapping("/member/member/info/{id}")
    public R info(@PathVariable("id") Long id);
}
