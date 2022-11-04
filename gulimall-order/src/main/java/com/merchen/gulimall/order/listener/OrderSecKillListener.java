package com.merchen.gulimall.order.listener;

import com.merchen.common.to.mq.SeckillOrderTo;
import com.merchen.gulimall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author MrChen
 * @create 2022-10-14 21:22
 */
@Slf4j
@Component
@RabbitListener(queues = "order.seckill.order.queue")
public class OrderSecKillListener {

    @Autowired
    OrderService orderService;
    @RabbitHandler
    public void SecKillQueue(SeckillOrderTo seckillOrderTo, Channel channel, Message message) throws IOException {
        log.info("准备创建秒杀订单的详细信息:{}",seckillOrderTo.getOrderSn());
        try {
            orderService.createSecKillOrder(seckillOrderTo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }catch (Exception e){
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
        //保存订单
        //支付订单
    }
}
