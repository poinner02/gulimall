package com.merchen.gulimall.thirdparty.controller;

import com.alibaba.fastjson.JSON;
import com.merchen.common.utils.R;
import com.merchen.gulimall.thirdparty.component.SmsComponent;
import org.apache.http.HttpEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



/**
 * @author MrChen
 * @create 2022-08-16 19:54
 */

@RestController
@RequestMapping("/sms")
public class ShortMessageController {

    @Autowired
    private SmsComponent smsComponent;

    @GetMapping("/sendCode")
    public R sendCode(@RequestParam("phone") String phone,@RequestParam("code") String code){
        HttpEntity httpEntity = smsComponent.senCode(phone, code);
        String s = httpEntity.toString();

        return R.ok();
    }

}
