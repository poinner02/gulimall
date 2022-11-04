package com.merchen.gulimall.search.controller;

import com.merchen.common.exception.BizCodeEnume;
import com.merchen.common.to.elasticSearch.EsModel;
import com.merchen.common.utils.R;
import com.merchen.gulimall.search.service.ElasticSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * @author MrChen
 * @create 2022-07-10 18:09
 */
@Slf4j
@RequestMapping("/elasticsearch")
@RestController
public class ElasticSearchController {

    @Autowired
    private ElasticSearchService elasticSearchService;

    @PostMapping("/saveEsModel")
    public R saveEs(@RequestBody List<EsModel> esModelList){
        Boolean save = false;
        try {
            save = elasticSearchService.save(esModelList);
        } catch (IOException e) {
            log.error("ElasticSearchController商品上架错误：{}",e);
            return R.error(BizCodeEnume.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnume.PRODUCT_UP_EXCEPTION.getMessage());
        }
        if(!save){
            //全部上架成功
            return  R.ok();
        }
        return R.error(BizCodeEnume.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnume.PRODUCT_UP_EXCEPTION.getMessage());
    }
}
