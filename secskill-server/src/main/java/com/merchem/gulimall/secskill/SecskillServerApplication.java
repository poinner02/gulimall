package com.merchem.gulimall.secskill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 *
 * 1.install mavn
 * <dependency>
 *     <groupId>com.alibaba.cloud</groupId>
 *     <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
 * </dependency>
 *
 * 2.启动sentinel dashboard 页面 java -jar sentinel-dashboard-1.6.3.jar --server.port=8033
 *
 * 3.配置yml
 * spring:
 *   cloud:
 *     sentinel:
 *       transport:
 *         port: 8719
 *         dashboard: localhost:8033
 *
 */
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class SecskillServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecskillServerApplication.class, args);
    }

}
