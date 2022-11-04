package com.merchen.gulimall.order.listener;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.merchen.gulimall.order.config.AlipayConfig;
import com.merchen.gulimall.order.entity.OrderEntity;
import com.merchen.gulimall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author MrChen
 * @create 2022-09-22 19:26
 */
@RabbitListener(queues = {"order.release.order.queue"})
@Component
public class OrderColseListener {

    @Autowired
    private AlipayConfig alipayConfig;
    @Autowired
    private OrderService orderService;

    /**
     *
     * 监听order.release.order.queue 队列
     * @param order
     * @param channel
     * @param message
     * @throws IOException
     */
    @RabbitHandler
    public void OrderRelease(OrderEntity order, Channel channel, Message message) throws IOException {
        try {
            /**
             * @param deliveryTag the tag from the received {@link com.rabbitmq.client.AMQP.Basic.GetOk} or {@link com.rabbitmq.client.AMQP.Basic.Deliver}
             * @param multiple true to acknowledge all messages up to and 批处理
             */
            //没有异常正常执行则ack消息
            orderService.releaseOrder(order);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            //支付宝手动关闭订单逻辑
            handleAlipayHandleClosePayMethod(order);
            //todo 订单服务延迟造成库存服务先解锁。
            
            //执行订单未支付原因等解锁订单
        }catch (RuntimeException | IOException | AlipayApiException e){
            /**
             * @param deliveryTag the tag from the received {@link com.rabbitmq.client.AMQP.Basic.GetOk} or {@link com.rabbitmq.client.AMQP.Basic.Deliver}
             * @param requeue true if the rejected message should be requeued rather than discarded/dead-lettered  requeu为true则重新回到队列中
             */
            //异常回放消息
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);

        }
    }

    private void handleAlipayHandleClosePayMethod(OrderEntity order) throws AlipayApiException {
        //支付宝手动关闭订单逻辑
        AlipayClient alipayClient = new DefaultAlipayClient(alipayConfig.getGatewayUrl(),alipayConfig.getApp_id(),alipayConfig.getMerchant_private_key(),"json","GBK",alipayConfig.getAlipay_public_key(), alipayConfig.getSign_type());
        AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
        JSONObject bizContent = new JSONObject();
        String orderSn = order.getOrderSn();
        //支付宝和订单号任意一个，二选一
        bizContent.put("trade_no", orderSn);
        request.setBizContent(bizContent.toString());
        AlipayTradeCloseResponse response = alipayClient.execute(request);
        if(response.isSuccess()){
            System.out.println("支付宝手动关闭订单调用成功");
        } else {
            System.out.println("支付宝手动关闭订单调用失败");
        }
    }


}
