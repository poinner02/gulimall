package com.merchen.gulimall.search.service;

import com.merchen.gulimall.search.vo.SearchParm;
import com.merchen.gulimall.search.vo.SearchResult;

/**
 * @author MrChen
 * @create 2022-08-03 21:22
 */
public interface MallSearchService {

    SearchResult search(SearchParm searchParm);
}
