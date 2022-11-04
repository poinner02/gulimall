package com.merchen.gulimall.auth;

import org.apache.commons.codec.digest.Md5Crypt;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.reflections.vfs.SystemDir;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Scanner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class GlimallAuthServiceApplicationTests {

    @Test
    public void test(){
        BigDecimal a;
        BigDecimal b;
        BigDecimal sum = new BigDecimal(0);
        while(true){
            Scanner s = new Scanner(System.in);
            a = new BigDecimal(s.next());
            b = new BigDecimal(s.next());
            if("#".equals(a)||"#".equals(b)){
                break;
            }
            sum = sum.add(a.multiply(b));
            System.out.println(sum);
        }
    }

    @Test
   public  void contextLoads() {

        String ps ="$2a$10$ttZUio.3D/OyY7k10KUYge7CRzWOLicWN73uq7asrnHq951Ne.SOi";
        boolean matches = new BCryptPasswordEncoder().matches("111111", ps);
        System.out.println(matches);


    }

}
