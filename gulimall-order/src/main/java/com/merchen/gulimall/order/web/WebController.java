package com.merchen.gulimall.order.web;


import com.alipay.api.AlipayApiException;
import com.merchen.common.exception.LockStockException;
import com.merchen.gulimall.order.config.AlipayConfig;
import com.merchen.gulimall.order.service.OrderService;
import com.merchen.gulimall.order.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

/**
 * @author MrChen
 * @create 2022-08-30 21:42
 */
@Slf4j
@Controller
public class WebController {
    @Autowired
    private AlipayConfig alipayConfig;

    @Autowired
    OrderService orderService;

    /**
     * 提交订单
     * @param submitVo
     * @param model
     * @return
     */
    @PostMapping("/submitOrder")
    public String submitOrder(OrderSubmitVo submitVo,
                              Model model,
                              RedirectAttributes attributes){
//        log.info("submitOrder:{}",submitVo);
        //校验价格、防重
        try {
            OrderSubmitResponseVo orderSubmitResponseVo = orderService.submitOrder(submitVo);
            if(orderSubmitResponseVo.getCode()==0){
                model.addAttribute("order", orderSubmitResponseVo.getOrderEntity());
                return  "pay";
            }
            String msg = "提交失败原因：";
            switch (orderSubmitResponseVo.getCode()){
                case 1: msg+="toen过期";break;
                case 2: msg+="检验价格失败";break;
                case 3: msg+="没有库存锁成功";break;
                case 500: msg+="token非法";break;
            }
            attributes.addFlashAttribute("msg",msg);
            return  "redirect:http://order.gulimall.com/toTrade";
        } catch (Exception e) {
            attributes.addFlashAttribute("msg",e.getMessage());
            return  "redirect:http://order.gulimall.com/toTrade";
        }
    }

    @GetMapping("/{page}.html")
    public String toPage(@PathVariable("page") String page) {

        return page;
    }

    /**
     * 购物车结算页面
     * 优化线程池
     * @param model
     * @return
     */
    @GetMapping("/toTrade")
    public String toTrade(Model model) throws ExecutionException, InterruptedException {
        OrderVo orderVo =  orderService.getOrderVo();
        model.addAttribute("confirmOrder", orderVo);
        return "confirm";
    }

    @PostMapping("/saveAdress")
    public String saveMemberAdress(MemberReceiveAddressEntity memberReceiveAddressEntity) {

        orderService.saveAdress(memberReceiveAddressEntity);

        //无论是否保存都要返回页面
        return "redirect:http://order.gulimall.com/toTrade";
    }


}
