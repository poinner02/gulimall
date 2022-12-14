package com.merchen.gulimall.search;

import com.alibaba.fastjson.JSON;
import com.merchen.gulimall.search.config.ElasticSearchConfig;
import lombok.Data;
import lombok.ToString;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import springfox.documentation.spring.web.json.Json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallSearchApplicationTests {

    @Autowired
    private RestHighLevelClient client;



    /**
     * Auto-generated: 2022-07-07 22:27:11
     *
     * @author json.cn (i@json.cn)
     * @website http://www.json.cn/java2pojo/
     */
    @ToString
    @Data
    static class Account {

        private int account_number;
        private int balance;
        private String firstname;
        private String lastname;
        private int age;
        private String gender;
        private String address;
        private String employer;
        private String email;
        private String city;
        private String state;
    }

    @Test
    public void queryUser() throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //????????????
        searchRequest.indices("users");
        sourceBuilder.query(QueryBuilders.matchQuery("userName", "??????"));
        searchRequest.source(sourceBuilder);
        SearchResponse search = client.search(searchRequest, ElasticSearchConfig.COMMON_OPTIONS);
        SearchHit[] hits = search.getHits().getHits();
        List list = new ArrayList<User>();
        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();
            User user = JSON.parseObject(sourceAsString, User.class);
            list.add(user);
        }
        System.out.println(list);
    }

    @Test
    public void queryData() throws IOException {
        //??????????????????
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //????????????
        searchRequest.indices("bank");
        //??????????????????
//        sourceBuilder.query();
//        sourceBuilder.from();
//        sourceBuilder.size();
//        sourceBuilder.aggregations();
        sourceBuilder.query(QueryBuilders.matchQuery("address", "mill"));
        //??????????????????
        TermsAggregationBuilder ageAgg = AggregationBuilders.terms("ageAgg").field("age").size(10);
        sourceBuilder.aggregation(ageAgg);
        //??????????????????
        AvgAggregationBuilder balanceAvg = AggregationBuilders.avg("balanceAvg").field("balance");
        sourceBuilder.aggregation(balanceAvg);
        searchRequest.source(sourceBuilder);
        //??????
        SearchResponse search = client.search(searchRequest, ElasticSearchConfig.COMMON_OPTIONS);
//        System.out.println(search);
        SearchHit[] hits = search.getHits().getHits();
        List list = new ArrayList<Account>();
        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();
            Account account = JSON.parseObject(sourceAsString, Account.class);
            list.add(account);
        }
        System.out.println(list);
    }

    //???????????????es???
    @Test
    public void indexData() throws IOException {
        IndexRequest indexRequest = new IndexRequest("users");
        indexRequest.id("1");
//        indexRequest.source("username","??????","age",18,"gander","boy");
        User user = new User("??????", "boy", 18);
        //??????json
        String string = JSON.toJSONString(user);
        indexRequest.source(string, XContentType.JSON);

        IndexResponse index = client.index(indexRequest, ElasticSearchConfig.COMMON_OPTIONS);
        System.out.println(index);

    }

    @Data
    class User{
        private String userName;
        private String gender;
        private Integer age;

        public User(String userName, String gender, Integer age) {
            this.userName = userName;
            this.gender = gender;
            this.age = age;
        }
    }

    @Test
    public void contextLoads() {
        System.out.println(client);
    }

}
