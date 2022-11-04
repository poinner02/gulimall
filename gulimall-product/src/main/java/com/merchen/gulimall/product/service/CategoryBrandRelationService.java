package com.merchen.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.merchen.common.utils.PageUtils;
import com.merchen.gulimall.product.entity.BrandEntity;
import com.merchen.gulimall.product.entity.CategoryBrandRelationEntity;
import com.merchen.gulimall.product.vo.BrandVO;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author ChenZhongBo
 * @email 961208477@qq.com
 * @date 2022-06-04 20:05:18
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveDetail(CategoryBrandRelationEntity categoryBrandRelation);

    void updateCateGoryDetails(Long catId, String name);

    List<BrandEntity> getList(Long catId);
}

