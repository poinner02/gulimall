package com.merchen.gulimall.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author MrChen
 * @create 2022-06-05 19:25
 * 后端校验
 * 1.jsr303 javax.validation.constraints
 * 2.分组校验
 */
@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients
public class GulimallProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(GulimallProductApplication.class, args);
    }
}
