package com.merchen.gulimall.auth.service.serviceImp;

import com.google.common.primitives.Longs;
import com.merchen.common.constant.AuthServiceConstant;
import com.merchen.common.utils.R;
import com.merchen.gulimall.auth.feign.ThirdPartSmsFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author MrChen
 * @create 2022-08-16 22:00
 */
@Service
public class RegistServiceImp implements RegistService{

    @Autowired
    private ThirdPartSmsFeignService thirdPartSmsFeignService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public R sendCode(String phone) {

        String code = UUID.randomUUID().toString().substring(0,4);

        String redisCode = redisTemplate.opsForValue().get(AuthServiceConstant.SMS_CODE_CACHE_PREFIX + phone);

        if(!StringUtils.isEmpty(redisCode)){
            //接口防刷
            long l = System.currentTimeMillis() - Long.parseLong(redisCode.split("_")[1]);
            if(l <120000){
                //不可以发送
                return R.error(40000, "操作过于频繁");
            }else{
                //发送
                R r = thirdPartSmsFeignService.sendCode(phone, code);
                redisTemplate.opsForValue().setIfAbsent(AuthServiceConstant.SMS_CODE_CACHE_PREFIX + phone, code+"_"+System.currentTimeMillis(), 120, TimeUnit.SECONDS);
                return r;
            }
        }else{
            //获取验证码存放到缓存中
            //直接发送短信
            R r = thirdPartSmsFeignService.sendCode(phone, code);
            redisTemplate.opsForValue().setIfAbsent(AuthServiceConstant.SMS_CODE_CACHE_PREFIX + phone, code+"_"+System.currentTimeMillis(), 120, TimeUnit.SECONDS);
            return r;

        }


    }

    public Boolean checkCode(String phone,String verifyCode) {
        String s = redisTemplate.opsForValue().get(AuthServiceConstant.SMS_CODE_CACHE_PREFIX + phone);
        if(StringUtils.isEmpty(s)){
            //验证码过期
            return false;
        }else{
            if( s.equals(verifyCode)){
                //校验成功
                return true;
            }else{
                return false;
            }
        }
    }
}
