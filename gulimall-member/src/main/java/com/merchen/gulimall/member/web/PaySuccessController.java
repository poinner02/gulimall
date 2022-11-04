package com.merchen.gulimall.member.web;

import com.merchen.common.utils.R;
import com.merchen.gulimall.member.feign.OrderFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @author MrChen
 * @create 2022-09-26 20:15
 */
@Controller
public class PaySuccessController {
    @Autowired
    private OrderFeignService orderFeignService;

    @GetMapping("/memberOrder.html")
    public String getpayOrderItems(@RequestParam Map<String,Object> parms,
                           Model model){
        R r = orderFeignService.listWitchOrderItem(parms);
        model.addAttribute("orders",r);
        return "orderList";
    }
}
