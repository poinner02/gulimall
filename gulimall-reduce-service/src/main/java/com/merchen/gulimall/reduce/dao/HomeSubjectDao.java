package com.merchen.gulimall.reduce.dao;

import com.merchen.gulimall.reduce.entity.HomeSubjectEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 首页专题表【jd首页下面很多专题，每个专题链接新的页面，展示专题商品信息】
 * 
 * @author ChenZhongBo
 * @email 961208477@qq.com
 * @date 2022-09-02 19:17:23
 */
@Mapper
public interface HomeSubjectDao extends BaseMapper<HomeSubjectEntity> {
	
}
