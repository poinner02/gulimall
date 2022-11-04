package com.merchem.gulimall.secskill.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * 消息队列设置
 * @author MrChen
 * @create 2022-08-30 20:15
 */
@Configuration
public class MyMQConf {


    @Bean
    MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

}
