package com.merchen.gulimall.member.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.merchen.common.to.MemberTO;

import com.merchen.gulimall.member.exception.LoginException;
import com.merchen.gulimall.member.exception.PhoneExistException;
import com.merchen.gulimall.member.exception.UserNameExistException;
import com.merchen.gulimall.member.feign.CouponFeignService;

import com.merchen.gulimall.member.vo.LoinUserVo;
import com.merchen.gulimall.member.vo.RegistUserVo;
import com.merchen.gulimall.member.vo.SocialUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.merchen.gulimall.member.entity.MemberEntity;
import com.merchen.gulimall.member.service.MemberService;
import com.merchen.common.utils.PageUtils;
import com.merchen.common.utils.R;



/**
 * 会员
 *
 * @author ChenZhongBo
 * @email 961208477@qq.com
 * @date 2022-06-05 19:10:51
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;


    @Autowired
    private CouponFeignService couponFeignService;

    @PostMapping("/oauth2/weixin/login")
    public R socialUser(@RequestParam("access_token") String access_token, @RequestParam("openid") String openid){
        MemberEntity memberEntity = memberService.socialLogin(access_token,openid);
        if(memberEntity != null ){
            return R.ok().setDate(memberEntity);
        }else{
            return R.error("社交登录失败错误");
        }
    }

    @PostMapping("/oauth2/weibo/login")
    public R oauth2Login(@RequestBody SocialUser socialUser){
        MemberEntity memberEntity = memberService.socialLogin(socialUser);
        if(memberEntity != null ){
            return R.ok().setDate(memberEntity);
        }else{
            return R.error("社交登录失败错误");
        }
    }

    @PostMapping("/remote/login")
    public R loginRemote(@RequestBody LoinUserVo user){
        try {
            MemberEntity memberEntity = memberService.checkLoginAccount(user);
            return R.ok().setDate(memberEntity);
        } catch (LoginException e) {
//            e.printStackTrace();
            return R.error().put("errors", e.getMessage());
        }

    }


    @PostMapping("/remote/regist")
    public  R registRemote(@RequestBody RegistUserVo user){
        try {
            memberService.regist(user);
            return R.ok();
        }catch (PhoneExistException e){
            return R.error().put("password",e.getMessage());
        }catch (UserNameExistException e){
            return R.error().put("username",e.getMessage());
        }catch (Exception e){
            return R.error();
        }
    }

    //远程接口查询用户信息
    @RequestMapping("/list/name")
    public R getMembersRemote(String name){

        List<MemberTO> list = memberService.getListaByName(name);
        return R.ok().setDate(list);
    }


    //todo
    @RequestMapping("/remote/list")
    public R remoteList(@RequestParam Map<String, Object> params){
        R list = couponFeignService.list();

        return list;
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody MemberEntity member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody MemberEntity member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
