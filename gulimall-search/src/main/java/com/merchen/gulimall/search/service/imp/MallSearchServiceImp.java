package com.merchen.gulimall.search.service.imp;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.merchen.common.to.elasticSearch.EsModel;
import com.merchen.common.utils.R;
import com.merchen.gulimall.search.config.ElasticSearchConfig;
import com.merchen.gulimall.search.constant.EsConstant;
import com.merchen.gulimall.search.feign.AttrFenginService;
import com.merchen.gulimall.search.service.MallSearchService;
import com.merchen.gulimall.search.vo.AttrResponseVO;
import com.merchen.gulimall.search.vo.SearchParm;
import com.merchen.gulimall.search.vo.SearchResult;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author MrChen
 * @create 2022-08-03 21:22
 */
//todo
@Service
public class MallSearchServiceImp implements MallSearchService {

    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private AttrFenginService attrFenginService;

    @Override
    public SearchResult search(SearchParm searchParm) {
        SearchResult searchResult = null;
        //??????????????????
        SearchRequest searchRequest = buildSearchRequest(searchParm);
        try {
            SearchResponse response = client.search(searchRequest, ElasticSearchConfig.COMMON_OPTIONS);
            //?????????????????????????????????????????????
            searchResult = buildSearchResult(response, searchParm);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return searchResult;
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param response
     * @return
     */
    private SearchResult buildSearchResult(SearchResponse response, SearchParm searchParm) {
        SearchResult searchResult = new SearchResult();
        SearchHits hits = response.getHits();
        //?????????????????????
        List<EsModel> esModels = new ArrayList<>();
        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();
            EsModel esModel = JSON.parseObject(sourceAsString, EsModel.class);
            if (!StringUtils.isEmpty(searchParm.getKeyword())) {
                //????????????
                String skuTitle = hit.getHighlightFields().get("skuTitle").fragments()[0].toString();
                esModel.setSkuTitle(skuTitle);
            }
            esModels.add(esModel);
        }
        searchResult.setProducts(esModels);
        //??????????????????
        Aggregations aggregations = response.getAggregations();
        ParsedLongTerms brand_agg = aggregations.get("brand_agg");
        List<? extends Terms.Bucket> buckets = brand_agg.getBuckets();
        List<SearchResult.BrandVO> brandVOS = new ArrayList<>();
        for (Terms.Bucket bucket : buckets) {
            //???????????????id
            String keyAsString = bucket.getKeyAsString();
            Long brandId = Long.parseLong(keyAsString);
            SearchResult.BrandVO brandVO = new SearchResult.BrandVO();
            brandVO.setBrandId(brandId);
            //?????????????????????
            ParsedStringTerms brand_name_agg = bucket.getAggregations().get("brand_name_agg");
            String branName = brand_name_agg.getBuckets().get(0).getKeyAsString();
            brandVO.setBrandName(branName);
            //?????????????????????
            ParsedStringTerms brandImg_agg = bucket.getAggregations().get("brandImg_agg");
            String brandImg = brandImg_agg.getBuckets().get(0).getKeyAsString();
            brandVO.setBrandImg(brandImg);
            brandVOS.add(brandVO);
        }
        searchResult.setBrands(brandVOS);
        //??????????????????
        ParsedLongTerms category_agg = aggregations.get("category_agg");
        List<SearchResult.CategoryVO> categoryVOS = new ArrayList<>();
        for (Terms.Bucket categoryAggBucket : category_agg.getBuckets()) {
            //??????categoryId
            SearchResult.CategoryVO categoryVO = new SearchResult.CategoryVO();
            Long categoryId = Long.parseLong(categoryAggBucket.getKeyAsString());
            categoryVO.setCatalogId(categoryId);
            //??????categoryName
            ParsedStringTerms category_name_agg = categoryAggBucket.getAggregations().get("category_name_agg");
            String categoryname = category_name_agg.getBuckets().get(0).getKeyAsString();
            categoryVO.setCatalogName(categoryname);
            categoryVOS.add(categoryVO);
        }
        searchResult.setCategorys(categoryVOS);
        //todo ??????????????????
        List<SearchResult.AttrVO> attrVOS = new ArrayList<>();
        ParsedNested attr_agg = aggregations.get("attr_agg");
        ParsedLongTerms attr_id_agg = attr_agg.getAggregations().get("attr_id_agg");
        for (Terms.Bucket attrIdAggBucket : attr_id_agg.getBuckets()) {
            //????????????id
            Long attrId = Long.parseLong(attrIdAggBucket.getKeyAsString());
            //???????????????
            String attrName = ((ParsedStringTerms) attrIdAggBucket.getAggregations().get("attr_name_agg")).getBuckets().get(0).getKeyAsString();
            //???????????????
            List<? extends Terms.Bucket> attr_value_agg = ((ParsedStringTerms) attrIdAggBucket.getAggregations().get("attr_value_agg")).getBuckets();
            List<String> attrValues = new ArrayList<>();
            for (Terms.Bucket bucket : attr_value_agg) {
                attrValues.add(bucket.getKeyAsString());
            }
            SearchResult.AttrVO attrVO = new SearchResult.AttrVO();
            attrVO.setAttrId(attrId);
            attrVO.setAttrName(attrName);
            attrVO.setAttrValue(attrValues);
            attrVOS.add(attrVO);
        }
        searchResult.setAttrVOS(attrVOS);
        //??????????????????
        long total = hits.getTotalHits().value;
        searchResult.setTotal(total);
        //???????????????
        Integer pageNumber = searchParm.getPageNumber();
        searchResult.setPageNum(pageNumber);
        //???????????????
        //10????????? ??????2?????????5???
        long pageTotalNum = total % EsConstant.PRODUCT_PAGESIZE == 0 ? total / EsConstant.PRODUCT_PAGESIZE : (total / EsConstant.PRODUCT_PAGESIZE + 1);
        searchResult.setTotalPageNum(pageTotalNum);
        List<Long> pageNavs = new ArrayList<>();
        for (long i = 0; i < pageTotalNum; i++) {
            pageNavs.add(i + 1);
        }
        searchResult.setPageNavs(pageNavs);
        //?????????????????????
        /**
         * ?????????????????????
         * ??????????????????????????????????????????link??????
         * ?????????
         * attrs[1_5???:6???,2_16G:18G]
         */
        if (searchParm.getAttrs() != null && searchParm.getAttrs().size() > 0) {

            List<SearchResult.NavVo> collect = searchParm.getAttrs().stream().map(attr -> {
                SearchResult.NavVo navVo = new SearchResult.NavVo();
                //????????????????????????attr?????? attrs[1_5???:6???,2_16G:18G]
                String[] s = attr.split("_");
                //??????????????????r??????
                R r = attrFenginService.info(Long.parseLong(s[0]));
                if (r.getCode() == 0) {
                    AttrResponseVO attrResponseVO = r.getData("attr", new TypeReference<AttrResponseVO>() {
                    });
                    navVo.setNavName(attrResponseVO.getAttrName());
                } else {
                    navVo.setNavName(s[0]);
                }
                navVo.setNavValue(s[1]);
                //??????link,???link?????????
                //??????????????????????????????????????????????????????????????????????????????url??????
                //??????bug
                try {
                    String encode = URLEncoder.encode(attr, "utf-8");
                    encode.replace("+", "%20");//????????????java????????????????????????
                    String replace = searchParm.get_queryString().replace("&attrs=" + encode, "");
                    navVo.setLink("http://search.gulimall.com/search.html?" + replace);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return navVo;
            }).collect(Collectors.toList());
            searchResult.setNavs(collect);
        }

        return searchResult;
    }

    /**
     * ??????????????????
     *
     * @return
     */
    private SearchRequest buildSearchRequest(SearchParm searchParm) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //??????dsl
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        String keyword = searchParm.getKeyword();
        if (!StringUtils.isEmpty(keyword)) {
            //??????????????????
            boolQueryBuilder.must(QueryBuilders.matchQuery("skuTitle", searchParm.getKeyword()));
        }
        //??????id
        List<Long> brandIds = searchParm.getBrandIds();
        if (brandIds != null && brandIds.size() > 0) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId", brandIds));
        }
        //??????id
        Long catalog3Id = searchParm.getCatalog3Id();
        if (catalog3Id != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("catalogId", catalog3Id));
        }
        // ???????????????attrs
        /**
         * ?????????
         * attrs[1_5???:6???,2_16G:18G]
         */
        List<String> attrs = searchParm.getAttrs();
        if (attrs != null && attrs.size() > 0) {
            for (String attr : attrs) {
                String[] s = attr.split("_");
                String[] split = null;
                if (s[1].contains(":")) {
                    split = s[1].split(":");
                } else {
                    split = new String[1];
                    split[0] = s[1];
                }
                BoolQueryBuilder nestedboolQuery = QueryBuilders.boolQuery();
                nestedboolQuery.must(QueryBuilders.termQuery("attrs.attrId", s[0]));
                nestedboolQuery.must(QueryBuilders.termsQuery("attrs.attrValue", split));
                NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs", nestedboolQuery, ScoreMode.None);
                boolQueryBuilder.filter(nestedQuery);
            }
        }
        //?????????????????????
        Integer hasStock = searchParm.getHasStock();
        if (hasStock != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("hasStock", hasStock.intValue() == 1));
        }
        //??????????????????
        /**
         *  /**
         *      * ??????????????????
         *      * ?????????
         *      *  _500,1_500,500_
         */

        String skuPrice = searchParm.getSkuPrice();
        if (!StringUtils.isEmpty(skuPrice)) {
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");
            String[] s = skuPrice.split("_");
            if(s.length > 0){
                //??????????????????
                if (skuPrice.startsWith("_")) {
                    rangeQuery.lte(s[1]);
                } else if (skuPrice.endsWith("_")) {
                    rangeQuery.gte(s[0]);
                } else {
                    rangeQuery.gte(s[0]).lte(s[1]);
                }
                boolQueryBuilder.filter(rangeQuery);
            }
        }
        /**
         * ??????sort??????
         * ???????????????????????????????????????
         * ??????:
         *  saleCount_asc/skuPrice_desc
         */
        String sort = searchParm.getSort();
        if (!StringUtils.isEmpty(sort)) {
            //??????sort
            String[] split = sort.split("_");
            SortOrder order = split[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC;
            sourceBuilder.sort(split[0], order);
        }
        //??????
        /**
         * pageSize 5
         * pageNum:1 from:0 size:5   [0,1,2,3,4]
         * pageNum:2 from:5 size:5
         * from = (pageNum-1)*pageSize
         */
        sourceBuilder.from((searchParm.getPageNumber() - 1) * EsConstant.PRODUCT_PAGESIZE);
        //??????2???
        sourceBuilder.size(EsConstant.PRODUCT_PAGESIZE);
        sourceBuilder.query(boolQueryBuilder);
        //??????
        if (!StringUtils.isEmpty(searchParm.getKeyword())) {
            HighlightBuilder builder = new HighlightBuilder();
            builder.field("skuTitle");
            builder.preTags("<b style='color:red'>");
            builder.postTags("</b>");
            sourceBuilder.highlighter(builder);
        }
        //????????????
        TermsAggregationBuilder branagg = AggregationBuilders.terms("brand_agg").field("brandId").size(10);
        branagg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(10));
        branagg.subAggregation(AggregationBuilders.terms("brandImg_agg").field("brandImg").size(10));
        sourceBuilder.aggregation(branagg);
        //????????????
        TermsAggregationBuilder categorygg = AggregationBuilders.terms("category_agg");
        categorygg.field("catalogId").size(10);
        categorygg.subAggregation(AggregationBuilders.terms("category_name_agg").field("catalogName").size(1));
        sourceBuilder.aggregation(categorygg);
        //????????????
        NestedAggregationBuilder attrnestedagg = AggregationBuilders.nested("attr_agg", "attrs");
        TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId").size(10);
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(10));
        attrnestedagg.subAggregation(attr_id_agg);
        sourceBuilder.aggregation(attrnestedagg);


//        System.out.println("?????????dsl:" + sourceBuilder.toString());
        SearchRequest searchRequest = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, sourceBuilder);
        return searchRequest;
    }
}
