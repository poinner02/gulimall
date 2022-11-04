package com.merchen.gulimall.weixinservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * @author MrChen
 * @create 2022-08-21 21:20
 */
@Configuration
public class SessionConfig {

    // tag::cookie-serializer[]
    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName("GULIJSESSIONID"); // <1>
        serializer.setDomainName("gulimall.com"); // <3>
        return serializer;
    }
    // end::cookie-serializer[]


    @Bean
    public RedisSerializer springSessionDefaultRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }
}
