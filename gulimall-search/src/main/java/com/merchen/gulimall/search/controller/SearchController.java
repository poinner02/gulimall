package com.merchen.gulimall.search.controller;

import com.merchen.gulimall.search.service.MallSearchService;
import com.merchen.gulimall.search.vo.SearchParm;
import com.merchen.gulimall.search.vo.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;


/**
 * @author MrChen
 * @create 2022-08-03 20:20
 */

@Slf4j
@Controller
public class SearchController {

    @Autowired
    private MallSearchService mallSearchService;

    @GetMapping("/search.html")
    public String search(SearchParm searchParm, ModelMap modelMap, HttpServletRequest request){
        searchParm.set_queryString(request.getQueryString());
        SearchResult result = mallSearchService.search(searchParm);
        modelMap.addAttribute("searchResult", result);
        return "list";
    }
}
