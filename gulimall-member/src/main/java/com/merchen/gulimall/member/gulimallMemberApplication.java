package com.merchen.gulimall.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author MrChen
 * @create 2022-06-05 19:25
 */
@EnableFeignClients
@SpringBootApplication
@EnableDiscoveryClient
public class gulimallMemberApplication {
    public static void main(String[] args) {
        SpringApplication.run(gulimallMemberApplication.class, args);
    }
}
