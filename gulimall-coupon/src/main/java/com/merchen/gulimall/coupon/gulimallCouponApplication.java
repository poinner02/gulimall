package com.merchen.gulimall.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 1.如何使用nacos作为配置中心作为管理
 * 2.依赖 <dependency>
 *             <groupId>com.alibaba.cloud</groupId>
 *             <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
 *         </dependency>
 * 3.创建bootstrap.properties
 * 4.配置spring:
 *   cloud:
 *     nacos:
 *       config:
 *         server-addr: localhost:8848
 *         file-extension: properties
 *   application:
 *     name: gulimall-coupon
 * 5.配置中心默认添加 一个叫数据集
 * 6.加注解@RefreshScope
 * 7.如果配置中心和当前配置中心都有，优先使用配置中心
 *
 *
 * 细节
 * 1 命名空间
 *      默认public
 *      1) 开发 、测试、生产
 *      2) 利用命名空间做环境隔离 ,在bootstrap.yml 配置 spring.cloud.nacos.config.namespace
 * 2 配置集:所有的配置集合
 * 3 配置集ID：类似文件名字
 *     Data ID ：文件名
 * 4 配置分组：
 *      默认的所有的配置集都属于：DEFAULT_GROUP:
 *
 * 每个微服务创建自己的命名空间，使用配置分组区分环境 test 、dev、 pro
 *
 * 5 同时加载 多个配置集
 *
 * @author MrChen
 * @create 2022-06-05 18:12
 */
@EnableDiscoveryClient
@SpringBootApplication
public class gulimallCouponApplication {
    public static void main(String[] args) {
        SpringApplication.run(gulimallCouponApplication.class, args);
    }

}
