package com.merchen.gulimall.member.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;


/**
 * 解决远程调用丢失请求头信息
 * @author MrChen
 * @create 2022-09-06 11:50
 */
@Configuration
public class GuliFeignConfig {

    @Bean("requestInterceptor")
    public  RequestInterceptor requestInterceptor(){
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                //RequestContextHolder 获取request
                ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if(requestAttributes!=null){
                    HttpServletRequest request = requestAttributes.getRequest();
                    String cookie = request.getHeader("Cookie");
                    //同步请求头，cookie
                    requestTemplate.header("Cookie",cookie);
                }
            }
        };
    }
}
