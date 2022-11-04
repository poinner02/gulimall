package com.merchen.gulimall.order.config;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * 自定义消息序列化成json
 * @author MrChen
 * @create 2022-08-29 21:21
 */
@Configuration
public class MyMQMessageConvertConf {
//    @Autowired
//    private RabbitTemplate rabbitTemplate;
    //自定义消息序列化成json
    @Bean
    MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 定制自己的rabbitmq
     */
//    @PostConstruct //MyRabbitConfig对象创建完成后，执行这个方法
//    public  void initRabbitMQ(){
//        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
//            /**
//             *
//             * @param correlationData  当前消息唯一关联数据（当前消息唯一的id）
//             * @param ack 消息是否成功收到
//             * @param cause 失败的原因
//             */
//            @Override
//            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
//                System.out.println("confirtm:"+correlationData+"ack="+ack+"cause="+cause);
//            }
//        });
//    }
}
