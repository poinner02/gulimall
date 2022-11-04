package com.merchen.gulimall.product.web;

import com.merchen.gulimall.product.exception.ErrorException;
import com.merchen.gulimall.product.service.SkuInfoService;
import com.merchen.gulimall.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author MrChen
 * @create 2022-08-10 21:37
 */
@Controller
public class ItemController {

    @Autowired
    private SkuInfoService skuInfoService;

    @GetMapping("/{skuId}.html")
    public  String skuItem(@PathVariable("skuId")Long skuId, ModelMap map){
        SkuItemVo vo = null;
        try {
            vo = skuInfoService.item(skuId);
        } catch (ExecutionException e) {
//            e.printStackTrace();
            throw  new ErrorException(e.getMessage().toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        map.addAttribute("item", vo);
        return "item";
    }


}
