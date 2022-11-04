package com.merchem.gulimall.secskill.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 定时任务配置中心
 * 异步方式调度
 * @author MrChen
 * @create 2022-09-30 23:58
 */
@Configuration
@EnableAsync
@EnableScheduling
public class ScheduleConf {

}
