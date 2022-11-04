package com.merchem.gulimall.secskill.Schedule;

import com.merchem.gulimall.secskill.service.SecSkillService;
import com.merchen.common.utils.R;
import jodd.time.TimeUtil;
import lombok.extern.slf4j.Slf4j;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;


/**
 * @author MrChen
 * @create 2022-09-30 23:58
 */

@Slf4j
@Component
public class SecKillSchedule {

    @Autowired
    private SecSkillService secSkillService;

    //分布式锁
    private final String upload_lock = "seckill:upload:lock";

    @Autowired
    private RedissonClient client;

    /**
     * 分布式锁测试成功
     *
     */
    @Async
    @Scheduled(cron = "0 * * * * ?")
    public void getRecenty3Day(){
        //分布式锁
        //创建分布式锁
        RLock lock = client.getLock(upload_lock);
        lock.lock( );//阻塞式等待，默认30s释放，无需处理掉电或者程序问题导致锁失效问题
        boolean b = false;
        try {
            b = lock.tryLock(100, 10, TimeUnit.SECONDS);
            if (b) {
                //抢占分布式锁成功
                secSkillService.getSecSkill3DayCoupon();
            }
        } catch (InterruptedException e) {
//            e.printStackTrace();
            log.info("秒杀商品上架分布式锁问题异常：{}", e.getMessage());
        }finally {
            lock.unlock();
        }
    }


}
