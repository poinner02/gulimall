package com.merchen.gulimall.order;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 1使用RabbitMQ
 * 2引入amqp场景,RabbitAutoConfiguration生效
 * 3 容器中自动配置了
 *      RabbitTemplate AmqpAdmin RabbitMessagingTemplate CachingConnectionFactory
 *      所有的属性在@ConfigurationProperties(prefix = "spring.rabbitmq")配置
 * 4 @EnableRabbit 开启功能
 * 5 @RabbitListener 监听消息，(必须要开启@EnableRabbit),类+方法上
 * 6 @RabbitHandler 标在方法上（重载）
 *
 * 开启代理模式调用事务
 * @EnableAspectJAutoProxy(exposeProxy = true)
 * @author MrChen
 * @create 2022-06-05 19:25
 */
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableFeignClients
@EnableRabbit
@EnableDiscoveryClient
@SpringBootApplication
public class GulimallOrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(GulimallOrderApplication.class, args);
    }
}
