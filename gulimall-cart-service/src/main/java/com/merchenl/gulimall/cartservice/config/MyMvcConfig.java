package com.merchenl.gulimall.cartservice.config;

import com.merchenl.gulimall.cartservice.interceptor.CartInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author MrChen
 * @create 2022-08-24 23:04
 */
@Configuration
public class MyMvcConfig implements WebMvcConfigurer {
    /**
     * 拦截当前微服务所有请求
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CartInterceptor()).addPathPatterns("/**");
    }
}
