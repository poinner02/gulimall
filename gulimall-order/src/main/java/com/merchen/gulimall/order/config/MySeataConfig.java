package com.merchen.gulimall.order.config;

import com.zaxxer.hikari.HikariDataSource;
//import io.seata.rm.datasource.DataSourceProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

/**
 * @author MrChen
 * @create 2022-09-16 22:35
 */
//@Configuration
//public class MySeataConfig {
//    @Autowired
//    DataSourceProperties dataSourceProperties;
//    @Bean
//    DataSource dataSource(DataSourceProperties properties){
//        HikariDataSource dataSource = properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
//        if (StringUtils.hasText(properties.getName())) {
//            dataSource.setPoolName(properties.getName());
//        }
//        return new DataSourceProxy(dataSource);
//    }
//}
