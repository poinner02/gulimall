package com.merchen.gulimall.ssoclient.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * @author MrChen
 * @create 2022-08-23 20:06
 */
@Controller
public class hellocontroller {
    @Value("${sso.server.url}")
    private String ssoServerUrl;
    /**
     * 测试无需登录
     * @return
     */
    @ResponseBody
    @GetMapping("/hello")
    public  String  hello(){
        return "hello";
    }
    /**
     * 客户端
     * @param model
     * @param session
     * @param token
     * @return
     */
    @GetMapping("/employees")
    public String employees(Model model,
                            HttpSession session,
                            @RequestParam(value = "token",required = false) String token){
        if(!StringUtils.isEmpty(token)){
            RestTemplate restTemplate = new RestTemplate();
            //发送请求给单点登陆服务器获取用户信息
            ResponseEntity<String> forEntity = restTemplate.getForEntity("http://sso.com:8080/info?token=" + token, String.class);
            String body = forEntity.getBody();
            session.setAttribute("loginUser",body);
        }
        Object user = session.getAttribute("loginUser");
        if(user==null){
            //没有登录,跳转到登录服务器登录
            return "redirect:"+ssoServerUrl+"?redirect_url=http://client1.com:8081/employees";
        }
        //模拟渲染页面数据
        /**
         *<!DOCTYPE html>
         * <html lang="en" xmlns:th="http://www.thymeleaf.org">
         * <head>
         *     <meta charset="UTF-8">
         *     <title>Title</title>
         * </head>
         * <body>
         *     <h1>欢迎：[[${session.loginUser}]]</h1>
         *     <ul>
         *         <li th:each="emp : ${emps}">name :[[${emp}]]</li>
         *     </ul>
         * </body>
         * </html>
         */
        List<String> emps =new ArrayList<>();
        emps.add("aa");
        emps.add("bb");
        emps.add("cc");
        model.addAttribute("emps", emps);
        return "list";
    }
}
