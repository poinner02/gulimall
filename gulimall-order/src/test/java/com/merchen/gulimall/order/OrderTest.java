package com.merchen.gulimall.order;


import lombok.extern.slf4j.Slf4j;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author MrChen
 * @create 2022-08-29 20:28
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class OrderTest {



//    @Autowired
//    RabbitTemplate rabbitTemplate;
//    @Autowired
//    AmqpAdmin amqpAdmin;
//    /**
//     * 1 创建Exchange Queue Binding
//     *      1) 使用amqp进行创建exchange
//     *      2）创建一个队列queue
//     * 2 如何收发消息
//     */
//    @Test
//    public void createExchange(){
//        amqpAdmin.declareExchange(new DirectExchange("hello-java-exchange",true,false));
//        log.info("exchange created[{}]", "hello-java-exchange");
//
//    }
//    @Test
//    public void createQueue(){
//        // public Queue(String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments)
//        //@param exclusive true if we are declaring an exclusive queue (the queue will only be used by the declarer's connection)
//        amqpAdmin.declareQueue(new Queue("hello-java-queue",true,false,true));
//        log.info("Queue created[{}]", "hello-java-queue");
//
//    }
//    /**
//     * destination；目的地
//     * destinationType: 目的类型
//     * exchange: 交换机
//     * routingKey: 路由键
//     * arguments: 参数
//     */
//    @Test
//    public void createBindRelation(){
//        //public Binding(String destination, DestinationType destinationType, String exchange, String routingKey,Map<String, Object> arguments)
//        //将exchange和destination绑定，使用routingKey路由键
//        amqpAdmin.declareBinding(new Binding("hello-java-queue", Binding.DestinationType.QUEUE,"hello-java-exchange","hello.java",null));
//        log.info("BindRelation created[{}]", "hello-java-bind");
//
//    }
//    @Test
//    public void send(){
//        for (int i = 0; i < 10; i++) {
//            if(i%2==0){
//                OrderReturnReasonEntity orderReturnReasonEntity = new OrderReturnReasonEntity();
//                orderReturnReasonEntity.setId(1L);
//                orderReturnReasonEntity.setCreateTime(new Date());
//                orderReturnReasonEntity.setSort(1);
//                orderReturnReasonEntity.setStatus(1);
//                orderReturnReasonEntity.setName("order-test:"+i);
//                rabbitTemplate.convertAndSend("hello-java-exchange","hello.java",orderReturnReasonEntity);
//            }else{
//                OrderEntity orderEntity = new OrderEntity();
//                rabbitTemplate.convertAndSend("hello-java-exchange","hello.java",orderEntity);
//            }
//            log.info("send[{}]", "send success");
//        }
//    }
}
