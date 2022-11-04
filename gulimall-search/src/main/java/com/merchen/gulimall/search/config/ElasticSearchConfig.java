package com.merchen.gulimall.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author MrChen
 * @create 2022-07-07 20:57
 */

@Configuration
public class ElasticSearchConfig {

    public   static  final RequestOptions COMMON_OPTIONS;
    static {
        RequestOptions.Builder builder  = RequestOptions.DEFAULT.toBuilder();
//        builder.addHeader("Authorization", "Bearer "+TOKEN);
//        builder.setHttpAsyncResponseConsumerFactory(
//                new HttpAsyncResponseConsumerFactory
//                        .HeapBufferedResponseConsumerFactory(30*1024*1024*1024));
        COMMON_OPTIONS = builder.build();
    }
    @Bean
    public RestHighLevelClient client(){
        RestClientBuilder builder = RestClient.builder(new HttpHost("192.168.182.130",9200,"http"));
        RestHighLevelClient client = new RestHighLevelClient(builder);
        return  client;
    }
}
