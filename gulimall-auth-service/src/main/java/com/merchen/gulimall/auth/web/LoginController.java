package com.merchen.gulimall.auth.web;

/**
 * @author MrChen
 * @create 2022-08-15 22:25
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.merchen.common.constant.AuthServiceConstant;
import com.merchen.common.to.MemberTO;
import com.merchen.common.utils.JwtUtils;
import com.merchen.common.utils.R;
import com.merchen.common.vo.MemberResponVo;
import com.merchen.gulimall.auth.exception.LoginException;
import com.merchen.gulimall.auth.exception.RegsitException;
import com.merchen.gulimall.auth.vo.LoinUserVo;
import com.merchen.gulimall.auth.vo.RegistUserVo;
import com.merchen.gulimall.auth.feign.MemberFeignService;
import com.merchen.gulimall.auth.feign.ThirdPartSmsFeignService;
import com.merchen.gulimall.auth.service.serviceImp.RegistServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller
public class LoginController {
    //远程接口
    @Autowired
    MemberFeignService memberFeignService;


    @Autowired
    ThirdPartSmsFeignService thirdPartSmsFeignService;


    @Autowired
    private RegistServiceImp registService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @ResponseBody
    @GetMapping("/sendCode")
    public R sendCode(@RequestParam("phone") String phone) {
        R r = registService.sendCode(phone);
        return r;
    }


    /**
     * todo 分布session问题
     * 表单提交注册用户
     * @param user
     * @param result
     * @param
     * @return
     */
    @PostMapping("/regist")
    public String registPage(@Validated RegistUserVo user, BindingResult result, RedirectAttributes attributes) throws RegsitException {
        //jsr303校验
        if (result.hasErrors()) {
            Map<String, String> errors = result.getFieldErrors().stream().collect(Collectors.toMap(k -> {
                return k.getField();
            }, v -> {
                return v.getDefaultMessage();
            }));
            //回显异常信息，只刷新一次
            attributes.addFlashAttribute("errors", errors);
            //表单提交不要用转发
//            return "regist";
            //重定向 域数据使用  RedirectAttributes
            return "redirect:http://auth.gulimall.com/regist.html";
        }

        //校验验证码
        String code = user.getCode();
        String redisCode = stringRedisTemplate.opsForValue().get(AuthServiceConstant.SMS_CODE_CACHE_PREFIX + user.getPhone());
        //验证码没有过期
        if(!StringUtils.isEmpty(redisCode)){
            //校验验证码
            if(code.equals(redisCode.substring(0, redisCode.indexOf("_")))){
                //删除缓存中的验证码
                stringRedisTemplate.delete(AuthServiceConstant.SMS_CODE_CACHE_PREFIX + user.getPhone());
            }else {
                //校验不通过
                throw new RegsitException("验证码不正确","code");
            }
        }else{
            throw new RegsitException("验证码过期重新输入","code");
        }

        //调用会员服务校验其他数据
        R r = memberFeignService.registRemote(user);
        if(r.getCode()!=0){
            //处理异常
            if(r.get("password")!=null){
                throw new RegsitException(r.get("password").toString(),"password");
            }
            if(r.get("username")!=null){
                throw new RegsitException(r.get("username").toString(),"username");
            }
        }
        //注册成功
        return "redirect:http://auth.gulimall.com/login.html";
    }


    /**
     * 登录页面
     * @param token
     * @return
     */
    @GetMapping("/login.html")
    public String toLoginPage(@CookieValue(name = "sso_token",required = false)String token){
//      if(!StringUtils.isEmpty(token)){
//          //直接去主页面
//          return  "redirect:http://gulimall.com/?sso_token="+token;
//      }else{
//          //否则去登陆页
//          return "login";
//      }
        return "login";
    }

    /**
     * 登录请求
     * @param user
     * @param result
     * @param attributes
     * @param response
     * @return
     * @throws LoginException
     */
    @PostMapping("/login")
    public String indexPage(@Validated LoinUserVo user,
                            BindingResult result,
                            RedirectAttributes attributes,
                            HttpServletResponse response,
                            HttpSession session) throws LoginException {

        //jsr303校验
        if (result.hasErrors()) {
            Map<String, String> errors = result.getFieldErrors().stream().collect(Collectors.toMap(k -> {
                return k.getField();
            }, v -> {
                return v.getDefaultMessage();
            }));
            //回显异常信息，只刷新一次
            attributes.addFlashAttribute("errors", errors);
            //表单提交不要用转发
//            return "regist";
            //重定向 域数据使用  RedirectAttributes
            return "redirect:http://auth.gulimall.com/login.html";
        }
        //远程登录校验
        R r = memberFeignService.loginRemote(user);
        if(r.getCode()!=0){
            throw new LoginException(r.get("errors").toString());
        }else{
            MemberResponVo data = r.getData("data", new TypeReference<MemberResponVo>() {
            });
            //生成一个token
            String uuid = UUID.randomUUID().toString().replace("-","");
//            String jwtToken = JwtUtils.getJwtToken(data.getId().toString(), data.getNickname());
            //将用户信息json化保存到redis中
//            stringRedisTemplate.opsForValue().set(uuid, JSON.toJSONString(data), 1, TimeUnit.DAYS);
            session.setAttribute(AuthServiceConstant.LOGIN_USER,data);
//            response.addCookie(new Cookie("sso_token", uuid));
//            return "redirect:http://gulimall.com/?sso_token="+uuid;
            return "redirect:http://gulimall.com";
        }
    }
}
