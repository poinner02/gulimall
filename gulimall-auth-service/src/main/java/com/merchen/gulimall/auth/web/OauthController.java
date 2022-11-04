package com.merchen.gulimall.auth.web;

import com.alibaba.fastjson.JSON;

import com.alibaba.fastjson.TypeReference;
import com.merchen.common.constant.AuthServiceConstant;
import com.merchen.common.utils.HttpUtils;
import com.merchen.common.utils.JwtUtils;
import com.merchen.common.utils.R;
import com.merchen.common.vo.MemberResponVo;
import com.merchen.gulimall.auth.feign.MemberFeignService;
import com.merchen.gulimall.auth.vo.SocialUser;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 微博第三方登录
 * @author MrChen
 * @create 2022-08-19 20:47
 */
@Controller
public class OauthController {

    @Autowired
    private MemberFeignService memberFeignService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    //todo 登录退出
    @GetMapping("/logout")
    public String logOut(HttpSession session){
        session.invalidate();
        return "redirect:http://auth.gulimall.com/login.html";
    }


    /**
     *
     * @param code
     * @param session
     * @return
     * @throws Exception
     */
    //https://api.weibo.com/oauth2/authorize?client_id=52110553&response_type=code&redirect_uri=http://auth.gulimall.com/oauth2.0/weibo/success
    @GetMapping("/oauth2.0/weibo/success")
    public String weibo(@RequestParam("code") String code, HttpSession session) throws Exception {

        Map<String, String> headers = new HashMap<String, String>();
        Map<String, String> bodys = new HashMap<String, String>();
        bodys.put("client_id", "52110553");
        bodys.put("client_secret", "c5c24d802416476431d7c15d1a4df7d7");
        bodys.put("grant_type","authorization_code");
        bodys.put("redirect_uri","http://auth.gulimall.com/oauth2.0/weibo/success");
        bodys.put("code",code);
        HttpResponse response = HttpUtils.doPost("https://api.weibo.com", "/oauth2/access_token", "post", headers, null, bodys);
        if(response.getStatusLine().getStatusCode() == 200){
            //获取token
            SocialUser socialUser = JSON.parseObject(EntityUtils.toString(response.getEntity()).toString(), SocialUser.class);
            //分2种情况①：db种没有当前的社交帐号关联②：已经有关联
            R r = memberFeignService.oauth2Login(socialUser);
            if(r.getCode() == 0){
                //社交登录或者注册成功
                MemberResponVo memberResponVo  = r.getData("data", new TypeReference<MemberResponVo>() {
                });
                session.setAttribute(AuthServiceConstant.LOGIN_USER,memberResponVo);
                return "redirect:http://gulimall.com";
            }else{
                return "redirect:http://auth.gulimall.com/login.html";
            }

        }else{
          return "redirect:http://auth.gulimall.com/login.html";
        }

    }

}
