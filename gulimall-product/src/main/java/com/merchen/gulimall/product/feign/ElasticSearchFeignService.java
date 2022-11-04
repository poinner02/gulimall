package com.merchen.gulimall.product.feign;

import com.merchen.common.to.elasticSearch.EsModel;
import com.merchen.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author MrChen
 * @create 2022-07-10 18:07
 */
@FeignClient("gulimall-search")
public interface ElasticSearchFeignService {

    @PostMapping("/elasticsearch/saveEsModel")
    public R saveEs(@RequestBody List<EsModel> esModelList);

}
