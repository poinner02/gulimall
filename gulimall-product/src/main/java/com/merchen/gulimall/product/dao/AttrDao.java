package com.merchen.gulimall.product.dao;

import com.merchen.gulimall.product.entity.AttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品属性
 * 
 * @author ChenZhongBo
 * @email 961208477@qq.com
 * @date 2022-06-04 20:05:18
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {

    List<Long> getFilerIds(@Param("nofilerIds") List<Long> nofilerIds);
}
