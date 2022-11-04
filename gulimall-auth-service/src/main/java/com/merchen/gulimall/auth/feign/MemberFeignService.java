package com.merchen.gulimall.auth.feign;

import com.merchen.common.utils.R;
import com.merchen.gulimall.auth.vo.LoinUserVo;
import com.merchen.gulimall.auth.vo.RegistUserVo;
import com.merchen.gulimall.auth.vo.SocialUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

/**
 * @author MrChen
 * @create 2022-08-15 22:59
 */

@FeignClient("gulimall-member")
@Component
public interface MemberFeignService {

    @PostMapping("member/member/remote/login")
    public R loginRemote(@RequestBody LoinUserVo user);

    @RequestMapping("member/member/list/name")
    public R getMembersRemote(String name);

    @PostMapping("member/member/remote/regist")
    public  R registRemote(@RequestBody RegistUserVo user);

    @PostMapping("/member/member/oauth2/weibo/login")
    public R oauth2Login(@RequestBody SocialUser socialUser);

    @PostMapping("/member/member/oauth2/weixin/login")
    public R socialUser(@RequestParam("access_token") String access_token, @RequestParam("openid") String openid);

    @RequestMapping("member/member/info/{id}")
    public R info(@PathVariable("id") Long id);
}
