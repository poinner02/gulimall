package com.merchen.gulimall.order.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * 消息队列设置
 * @author MrChen
 * @create 2022-08-30 20:15
 */
@Configuration
public class MyMQConfirmConf {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @PostConstruct  //RabbitConfirmConfig 对象创建完成后，执行这个方法
    public void initRabbitMQ(){
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                System.out.println("confirtm:"+correlationData+" ack="+ack+" cause="+cause);
            }
        });
        /**
         * 消息没有投递队列，就触发这个失败回调
         */
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                System.out.println("message:"+message+" replyCode="+replyCode+" replyText="+replyText+" exchange:"+exchange+" routingKey:"+routingKey);
            }
        });

    }
}
