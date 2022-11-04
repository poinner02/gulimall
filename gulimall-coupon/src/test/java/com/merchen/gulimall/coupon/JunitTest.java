package com.merchen.gulimall.coupon;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @author MrChen
 * @create 2022-10-01 0:16
 */
@SpringBootTest
public class JunitTest {

    @Test
    public void test(){
        LocalDateTime of = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        String format = of.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println(format);
        LocalDateTime of1 = LocalDateTime.of(LocalDate.now().plusDays(3), LocalTime.MAX);
        String format1 = of1.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println(format1);
    }
}
