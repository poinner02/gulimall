package com.merchen.gulimall.order.service;

import com.alipay.api.AlipayApiException;
import com.baomidou.mybatisplus.extension.service.IService;
import com.merchen.common.to.mq.SeckillOrderTo;
import com.merchen.common.utils.PageUtils;
import com.merchen.gulimall.order.entity.OrderEntity;
import com.merchen.gulimall.order.to.OrderCreateTo;
import com.merchen.gulimall.order.vo.*;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author ChenZhongBo
 * @email 961208477@qq.com
 * @date 2022-06-05 19:20:45
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    OrderVo getOrderVo() throws ExecutionException, InterruptedException;

    void saveAdress(MemberReceiveAddressEntity memberReceiveAddressEntity);

    OrderSubmitResponseVo submitOrder(OrderSubmitVo submitVo);

    void saveOrderAndOrderItems(OrderCreateTo order);

    void releaseOrder(OrderEntity order);

    PayVo payOrder(String orderSn);

    PageUtils listWitchOrderItem(Map<String, Object> params);

    String handleAlipayed(PayAsyncVo payAsyncVo) throws Exception;

    void createSecKillOrder(SeckillOrderTo seckillOrderTo);
}

