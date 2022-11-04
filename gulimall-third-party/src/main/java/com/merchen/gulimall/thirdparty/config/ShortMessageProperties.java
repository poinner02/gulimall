package com.merchen.gulimall.thirdparty.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author MrChen
 * @create 2022-08-16 20:05
 */
@Component
@Data
@ConfigurationProperties(prefix = "spring.cloud.alicloud.sanwang.sms")
public class ShortMessageProperties {
    private String appcode;
}
