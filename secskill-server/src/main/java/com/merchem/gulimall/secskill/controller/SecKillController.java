package com.merchem.gulimall.secskill.controller;


import com.merchem.gulimall.secskill.service.SecSkillService;
import com.merchem.gulimall.secskill.to.SecKillSkuRedisTo;
import com.merchen.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * @author MrChen
 * @create 2022-10-09 21:07
 */
@Controller
public class SecKillController {


    @Autowired
    private SecSkillService secSkillService;


    @ResponseBody
    @GetMapping({"/SecKill"})
    public R SecKill() {
        //获取当前场次的秒杀数据
        List<SecKillSkuRedisTo> currentSecKill = secSkillService.getCurrentSecKill();

        return R.ok().put("data",currentSecKill);
    }

    /**
     * 获取某个秒杀商品
     * @param skuId
     * @return
     */
    @ResponseBody
    @GetMapping("/get/seckill/{skuId}")
    public R getSecKillSkuInfo(@PathVariable("skuId")Long skuId){
        SecKillSkuRedisTo killSkuRedisTo = secSkillService.getSecKillSkuInfo(skuId);
        return R.ok().put("data",killSkuRedisTo);
    }


    //location.href="http://seckill.gulimall.com/kill?killId="+killId+"&key="+key+"&num="+num;
    @GetMapping("/kill")
    public String  secKill(@RequestParam("killId")String killId,
                          @RequestParam("key")String key,
                          @RequestParam("num")Integer num,
                     Model model){

        String orderId = secSkillService.secKill(killId,key,num);
        model.addAttribute("orderSn",orderId);
        return "secKillPay";
    }
}
