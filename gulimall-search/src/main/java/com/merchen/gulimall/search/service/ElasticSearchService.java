package com.merchen.gulimall.search.service;

import com.merchen.common.to.elasticSearch.EsModel;

import java.io.IOException;
import java.util.List;

/**
 * @author MrChen
 * @create 2022-07-10 17:58
 */
public interface ElasticSearchService {


    Boolean save(List<EsModel> esModelList) throws IOException;
}
