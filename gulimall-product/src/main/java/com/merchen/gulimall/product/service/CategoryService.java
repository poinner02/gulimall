package com.merchen.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.merchen.common.utils.PageUtils;
import com.merchen.gulimall.product.entity.CategoryEntity;
import com.merchen.gulimall.product.vo.CateLogory2VO;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author ChenZhongBo
 * @email 961208477@qq.com
 * @date 2022-06-04 20:05:18
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listWithTree();

    void removeMenuByIds(List<Long> asList);

    Long[] getCategoryPath(Long catelogId);

    void updateDetails(CategoryEntity category);

    List<CategoryEntity> getOneLeveL();

    Map<String , List<CateLogory2VO>> getcateGoryJson();
}

