package com.merchen.gulimall.order.listener;

import com.merchen.gulimall.order.service.OrderService;
import com.merchen.gulimall.order.vo.PayAsyncVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;



/**
 * @author MrChen
 * @create 2022-09-27 21:21
 */
@Slf4j
@RestController
public class OrderPayedListener {
    @Autowired
    private OrderService orderService;

    @PostMapping("/payed/notify")
    public String handleAlipayed(PayAsyncVo payAsyncVo) throws Exception {
        String result  = orderService.handleAlipayed(payAsyncVo);
//        log.info("支付宝通知成功。。。。。。。。。。。");
        return result;
    }
}
