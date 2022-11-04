package com.merchen.gulimall.weixinservice.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.merchen.common.constant.AuthServiceConstant;
import com.merchen.common.utils.HttpUtils;
import com.merchen.common.utils.R;

import com.merchen.common.vo.MemberResponVo;
import com.merchen.gulimall.weixinservice.feign.MemberFeignService;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sun.plugin.util.UIUtil;

import javax.servlet.http.HttpSession;
import javax.sql.rowset.spi.SyncResolver;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 微博第三方登录
 * @author MrChen
 * @create 2022-08-19 20:47
 */
@Controller
public class WeixinOauthController {

    @Autowired
    private MemberFeignService memberFeignService;


    @Autowired
    private StringRedisTemplate redisTemplate;


    /**
     * 请求url ： https://open.weixin.qq.com/connect/qrconnect?appid=wxed9954c01bb89b47&redirect_uri=http://localhost:8160/api/ucenter/wx/callback&response_type=code&scope=snsapi_login#wechat_redirect
     * 微信登录
     * @param code
     * @param session
     * @return
     * @throws Exception
     */
    @GetMapping("/api/ucenter/wx/callback")
    public String weixinLogin(@RequestParam ("code")String code,HttpSession session) throws Exception {
        //https://api.weixin.qq.com/sns/oauth2/access_token?appid=wxed9954c01bb89b47&secret=a7482517235173ddb4083788de60b90e&code=001eii1w3LQR5Z2iBE0w3pO7Xr4eii1w&grant_type=authorization_code
        Map<String, String> bodys = new HashMap<String, String>();
        bodys.put("appid", "wxed9954c01bb89b47");
        bodys.put("secret", "a7482517235173ddb4083788de60b90e");
        bodys.put("code",code);
        bodys.put("grant_type","authorization_code");
        HttpResponse res = HttpUtils.doGet("https://api.weixin.qq.com", "/sns/oauth2/access_token", "get", new HashMap<>(), bodys);
        if(res.getStatusLine().getStatusCode() == 200){
            JSONObject jsonObject = JSON.parseObject(EntityUtils.toString(res.getEntity()));
            String access_token = jsonObject.getString("access_token");
            String openid = jsonObject.getString("openid");
            R r = memberFeignService.socialUser(access_token,openid);
            if(r.getCode()==0){
                //社交登录或者注册成功
                MemberResponVo memberResponVo  = r.getData("data", new TypeReference<MemberResponVo>() {
                });
                String uuid = UUID.randomUUID().toString().replace("-","");
//                session.setAttribute(AuthServiceConstant.LOGIN_USER,memberResponVo);
                redisTemplate.opsForValue().set(uuid,JSON.toJSONString(memberResponVo),1, TimeUnit.DAYS);
                return "redirect:http://gulimall.com/?sso_token="+uuid;
            }else{
                return "redirect:http//auth.gulimall.com/login.html";
            }
        }else{
            return "redirect:http//auth.gulimall.com/login.html";
        }

    }


}
