package com.merchen.gulimall.product.dao;

import com.merchen.gulimall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author ChenZhongBo
 * @email 961208477@qq.com
 * @date 2022-06-04 20:05:18
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
