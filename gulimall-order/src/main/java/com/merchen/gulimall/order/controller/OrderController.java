package com.merchen.gulimall.order.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.merchen.gulimall.order.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.merchen.gulimall.order.entity.OrderEntity;
import com.merchen.gulimall.order.service.OrderService;
import com.merchen.common.utils.PageUtils;
import com.merchen.common.utils.R;



/**
 * 订单
 *
 * @author ChenZhongBo
 * @email 961208477@qq.com
 * @date 2022-06-05 19:20:45
 */
@Slf4j
@RestController
@RequestMapping("order/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 带分页查询
     * @param params
     * @return
     */
    @PostMapping("/listWitchOrderItem")
    public R listWitchOrderItem(@RequestBody Map<String, Object> params){
        PageUtils page = orderService.listWitchOrderItem(params);
        return R.ok().put("page", page);
    }

    //测试队列用的
//    @GetMapping("/send/message")
//    public String sendMessage(){
//
//        for (int i = 0; i < 10; i++) {
//            if(i%2==0){
//                OrderReturnReasonEntity orderReturnReasonEntity = new OrderReturnReasonEntity();
//                orderReturnReasonEntity.setId(1L);
//                orderReturnReasonEntity.setCreateTime(new Date());
//                orderReturnReasonEntity.setSort(1);
//                orderReturnReasonEntity.setStatus(1);
//                orderReturnReasonEntity.setName("order-test:"+i);
//                rabbitTemplate.convertAndSend("hello-java-exchange",
//                        "hello.java",
//                        orderReturnReasonEntity,
//                        new CorrelationData(UUID.randomUUID().toString()));
//            }else{
//                OrderEntity orderEntity = new OrderEntity();
//                rabbitTemplate.convertAndSend("hello-java-exchange","hello.java",orderEntity);
//            }
//            log.info("send[{}]", "send success");
//        }
//        return "ok";
//    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = orderService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		OrderEntity order = orderService.getById(id);

        return R.ok().put("order", order);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody OrderEntity order){
		orderService.save(order);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody OrderEntity order){
		orderService.updateById(order);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		orderService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    @GetMapping("/status/getStatus/{ordersn}")
    public R getStatus(@PathVariable("ordersn")String orderSn){
        OrderEntity order_sn = orderService.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
        return R.ok().put("data",order_sn);
    }

}
