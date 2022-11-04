package com.merchen.gulimall.order.web;

import com.alipay.api.AlipayApiException;
import com.merchen.gulimall.order.config.AlipayConfig;
import com.merchen.gulimall.order.service.OrderService;
import com.merchen.gulimall.order.vo.PayVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * 支付控制层
 * @author MrChen
 * @create 2022-09-26 19:33
 */
@Controller
public class PayController {

    @Autowired
    private AlipayConfig alipayConfig;

    @Autowired
    private OrderService orderService;

    @ResponseBody
    //produces 返回去的是html页面
    @GetMapping(value = "/payOrder",produces = "text/html")
    public String payOrder(@RequestParam("orderSn")String orderSn) throws AlipayApiException {
        PayVo payVo = orderService.payOrder(orderSn);
        String pay = alipayConfig.pay(payVo);
        return pay;
    }
}
