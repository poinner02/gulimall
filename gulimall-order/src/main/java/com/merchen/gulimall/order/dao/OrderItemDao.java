package com.merchen.gulimall.order.dao;

import com.merchen.gulimall.order.entity.OrderItemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单项信息
 * 
 * @author ChenZhongBo
 * @email 961208477@qq.com
 * @date 2022-06-05 19:20:45
 */
@Mapper
public interface OrderItemDao extends BaseMapper<OrderItemEntity> {
	
}
