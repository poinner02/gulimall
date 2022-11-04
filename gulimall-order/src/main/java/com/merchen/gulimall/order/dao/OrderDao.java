package com.merchen.gulimall.order.dao;

import com.merchen.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author ChenZhongBo
 * @email 961208477@qq.com
 * @date 2022-06-05 19:20:45
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
