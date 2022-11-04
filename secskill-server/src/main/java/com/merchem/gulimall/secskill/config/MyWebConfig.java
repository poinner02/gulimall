package com.merchem.gulimall.secskill.config;

import com.merchem.gulimall.secskill.interceptor.MyLoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author MrChen
 * @create 2022-09-01 19:50
 */
@Configuration
public class MyWebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //拦截所有请求
        registry.addInterceptor(new MyLoginInterceptor()).addPathPatterns("/**");
    }
}
