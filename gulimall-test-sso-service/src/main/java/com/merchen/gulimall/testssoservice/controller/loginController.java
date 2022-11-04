package com.merchen.gulimall.testssoservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @author MrChen
 * @create 2022-08-23 20:21
 */
@Controller
public class loginController {
    @Autowired
    private StringRedisTemplate redisTemplate;
    /**
     * 根据token去redis中获取用户信息
     * @param token
     * @return
     */
    @ResponseBody
    @GetMapping("/info")
    public String getUserInfo(@RequestParam("token")String token){
        String s = redisTemplate.opsForValue().get(token);
        return s;
    }
    /**
     * 感知登录功能
     * @param url 客户端的地址
     * @param model
     * @param sso_token
     * @return
     */
    @GetMapping("/login.html")
    public String loginPage(@RequestParam("redirect_url") String url,
                            Model model,
                            @CookieValue(value = "sso_token",required = false)String sso_token) {
        if(!StringUtils.isEmpty(sso_token)){
            //说明之前有人登录过，留下痕迹
            return "redirect:" + url+"?token="+sso_token;
        }
        model.addAttribute("url", url);
        return "login";
    }
    /**
     *
     * @param username
     * @param password
     * @param url 客户端的地址
     * @param response 设置cookie信息,做感知准备
     * @return
     */
    @PostMapping("/doLogin")
    public String doLogin(@RequestParam("username") String username,
                          @RequestParam("password") String password,
                          @RequestParam("url") String url,
                          HttpServletResponse response) {
        //模拟登录成功
        if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
            //user save
            String uuid = UUID.randomUUID().toString().replace("-", "");
            //redis中存放token
            redisTemplate.opsForValue().set(uuid,username);
            //当前域名下的cookie存放token信息给@GetMapping("/login.html")做感知登录
            response.addCookie(new Cookie("sso_token", uuid));
            //登录成功跳转到之前的页面
            return "redirect:" + url+"?token="+uuid;
        }
        return "login";
    }
}
