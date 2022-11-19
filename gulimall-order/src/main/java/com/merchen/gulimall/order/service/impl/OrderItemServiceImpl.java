package com.merchen.gulimall.order.service.impl;


import org.springframework.stereotype.Service;

import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.merchen.common.utils.PageUtils;
import com.merchen.common.utils.Query;

import com.merchen.gulimall.order.dao.OrderItemDao;
import com.merchen.gulimall.order.entity.OrderItemEntity;
import com.merchen.gulimall.order.service.OrderItemService;

/**
 *
 * @RabbitListener(queues = {"hello-java-queue"} )监听多个对列
 */
//@RabbitListener(queues = {"hello-java-queue"} )
@Service("orderItemService")
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {
    /**
     *
     * @param
     * @param  原生消息
     * @param 传输数据的通道
     * Queue 可以很多人监听，只要收到消息，队列删除消息，而且只能有一个收到此消息
     *                 1) 订单服务启动多个，同一个消息，只能一个客户端收到
     *                2） 只有一个消息处理完，方法运行结束，就可以接收到下一个消息
     */
//    @RabbitHandler
//    public void receiveMessage(Message message,
//                               OrderReturnReasonEntity content,
//                               Channel channel) {
//        System.out.println("接收消息:"+"   body:"+content);
//        //消息头属性
//        MessageProperties messageProperties = message.getMessageProperties();
//        try {
//            //模拟消费端手动确认消息机制，是偶数ack，否则nack
//            if(message.getMessageProperties().getDeliveryTag()%2==0){
//                /**
//                 * 手动签收 ack，非批量签收
//                 * basicAck(long deliveryTag, boolean multiple)
//                 * deliveryTag: channel内DeliveryTag按顺序自增
//                 * multiple： 批处理
//                 */
//                channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
//            }else{
//                /**
//                 * 手动不签 nack
//                 * basicNack(long deliveryTag, boolean multiple, boolean requeue)
//                 * multiple ：批处理
//                 * requeue :重新入队
//                 */
//                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false,false);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//    @RabbitHandler
//    public void receiveMessage2(Message message,
//                                OrderEntity content,
//                                Channel channel){
//        System.out.println("接收消息2:"+"   body:"+content);
//        try {
//            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
                new Query<OrderItemEntity>().getPage(params),
                new QueryWrapper<OrderItemEntity>()
        );

        return new PageUtils(page);
    }
}