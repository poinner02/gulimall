package com.merchen.gulimall.ware.listener;


import com.merchen.common.to.mq.OrderEntityTo;
import com.merchen.common.to.mq.StockLockTo;
import com.merchen.gulimall.ware.service.WareSkuService;
import com.merchen.gulimall.ware.vo.OrderTo;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author MrChen
 * @create 2022-09-21 21:51
 */
@Component
@RabbitListener(queues = {"stock.release.stock.queue"})
public class StockReleaseListener {

    @Autowired
    private WareSkuService wareSkuService;

    /**
     * 解锁库存
     *
     * @param stockLockTo
     * @param message
     */
    @RabbitHandler
    public void handleStockLockedReleaseWare(StockLockTo stockLockTo, Message message, Channel channel) throws IOException {
        try {
            wareSkuService.unLockStock(stockLockTo);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }catch (Exception e){
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }

    }
    /**
     * 解锁订单,由于网络波动会导致订单服务出现网络卡顿，库存服务可能会先解锁
     */
    @RabbitHandler
    public void handleOrderCloseRelease(OrderEntityTo order, Message message, Channel channel) throws IOException {
        try {
            wareSkuService.unLockStock(order);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }catch (Exception e){
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }

    }

}
