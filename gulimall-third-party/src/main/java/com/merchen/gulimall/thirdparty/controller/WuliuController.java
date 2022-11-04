package com.merchen.gulimall.thirdparty.controller;

import com.merchen.gulimall.thirdparty.component.WuliComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author MrChen
 * @create 2022-09-06 21:19
 */
@RestController
public class WuliuController {
    @Autowired
    WuliComponent wuliComponent;

    @GetMapping("/wuliu")
    public String getWuliuInfo(){
        String json = wuliComponent.wuliuinfo();

        return json;
    }
}
