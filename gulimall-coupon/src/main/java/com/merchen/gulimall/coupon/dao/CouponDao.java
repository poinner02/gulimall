package com.merchen.gulimall.coupon.dao;

import com.merchen.gulimall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author ChenZhongBo
 * @email 961208477@qq.com
 * @date 2022-06-05 18:08:58
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
