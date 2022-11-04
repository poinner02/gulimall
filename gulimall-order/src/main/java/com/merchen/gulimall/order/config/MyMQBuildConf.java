package com.merchen.gulimall.order.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;

/**
 * 消息队列的创建和绑定（包含死信队列）
 * 引入依赖
 *   <dependency>
 *             <groupId>org.springframework.boot</groupId>
 *             <artifactId>spring-boot-starter-aop</artifactId>
 *         </dependency>
 *  spring下直接使用Queue  Exchange Binding 装入ioc容器，自动配置
 *  注意属性发生变化，微服务重启，mq对应的关系不会发生变化，所以先删除，再重启
 *
 * @author MrChen
 * @create 2022-09-18 21:46
 */
@Configuration
public class MyMQBuildConf {
    /**
     * 延时接收队列
     * @return
     */
    @Bean
    public Queue orderDelayQueue(){
        HashMap<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", "order-event-exchange");
        arguments.put("x-dead-letter-routing-key", "order.release.order");
        arguments.put("x-message-ttl", 15000);//2分钟发送给死信队列
        /**
         * Construct a new queue, given a name, durability flag, and auto-delete flag, and arguments.
         * @param name the name of the queue - must not be null; set to "" to have the broker generate the name.
         * @param durable true if we are declaring a durable queue (the queue will survive a server restart)
         * @param exclusive true if we are declaring an exclusive queue (the queue will only be used by the declarer's
         * connection)
         * @param autoDelete true if the server should delete the queue when it is no longer in use
         * @param arguments the arguments used to declare the queue
         */
        return new Queue("order.delay.queue", true, false,false, arguments);
    }

    /**
     * 接收队列
     * @return
     */
    @Bean
    public Queue orderReleaseOrderQueue(){
        return new Queue("order.release.order.queue", true,false,false,null);
    }

    /**
     * top交换机
     * @return
     */
    @Bean
    public Exchange orderEventExchange(){
        return new TopicExchange("order-event-exchange", true,false);
    }

    /**
     * 绑定队列order.delay.queue和交换机order-event-exchange
     * @return
     */
    @Bean
    public Binding orderCreateOrderBind(){
        return new Binding("order.delay.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.create.order",
                null);
    }

    /**
     * 绑定队列order.release.order.queue和交换机order-event-exchange
     * @return
     */
    @Bean
    public Binding orderReleaseOrderBind(){
        return new Binding("order.release.order.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.release.order",
                null);
    }

    /**
     *
     * 订单交换机和库存延时队列进行绑定，路由key是order.release.other.#
     * @return
     */
    @Bean
    public Binding orderReleaseOtherBind(){
        return new Binding("stock.release.stock.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.release.other.#",
                null);
    }

    @Bean
    public Queue orderSeckillOrderQueue(){
        return new Queue("order.seckill.order.queue",true,false,false,null);
    }

    //order.seckill.order
    @Bean
    public Binding orderSeckillOrderBind(){
        return new Binding("order.seckill.order.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.seckill.order",
                null);
    }
}
