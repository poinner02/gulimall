package com.merchen.gulimall.search.service.imp;

import com.alibaba.fastjson.JSON;
import com.merchen.common.to.elasticSearch.EsModel;
import com.merchen.gulimall.search.config.ElasticSearchConfig;
import com.merchen.gulimall.search.constant.EsConstant;
import com.merchen.gulimall.search.service.ElasticSearchService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author MrChen
 * @create 2022-07-10 17:59
 */
@Slf4j
@Service
public class ElasticSearchServiceImp implements ElasticSearchService {

    @Autowired
    private RestHighLevelClient client;

    @Override
    public Boolean save(List<EsModel> esModelList) throws IOException {
        //ElasticSearch建立映射关系 保存在resources/product-mapping.txt中
        //批量保存BulkRequest bulkRequest, RequestOptions options
        BulkRequest bulkRequest = new BulkRequest();
        for (EsModel esModel : esModelList) {
            //创建索引
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            indexRequest.index(EsConstant.PRODUCT_INDEX);
            //指定id
            indexRequest.id(esModel.getSkuId().toString());
            String json = JSON.toJSONString(esModel);
            indexRequest.source(json,XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
        BulkResponse bulk = client.bulk(bulkRequest, ElasticSearchConfig.COMMON_OPTIONS);
        //todo 有错误处理
        boolean b = bulk.hasFailures();
        if(b){
            List<String> stringList = Arrays.stream(bulk.getItems()).map(item -> {
                return item.getId();
            }).collect(Collectors.toList());
            log.error("商品上架出错：{}",stringList);
        }
        //返回false是没有错误的
        return b;
    }

//    @Override
//    public <T> void saveObject(Object o, Class<T> tClass) throws IOException {
//        T item = (T) o;
//        String IndexName = tClass.getName();
//        IndexRequest indexRequest = new IndexRequest(IndexName);
////        indexRequest.id("1");
////        indexRequest.source("username","张三","age",18,"gander","boy");
////        User user = new User("张三", "boy", 18);
//        //转成json
//        String string = JSON.toJSONString(item);
//        indexRequest.source(string, XContentType.JSON);
//        IndexResponse index = client.index(indexRequest, ElasticSearchConfig.COMMON_OPTIONS);
//        System.out.println(index);
//    }
}
