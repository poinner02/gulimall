package com.merchen.gulimall.member.config;

import com.merchen.gulimall.member.interceptor.MyLoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author MrChen
 * @create 2022-09-26 20:33
 */
@Configuration
public class MemberConfig implements WebMvcConfigurer {

    @Autowired
    private MyLoginInterceptor myLoginInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(myLoginInterceptor);
    }
}
