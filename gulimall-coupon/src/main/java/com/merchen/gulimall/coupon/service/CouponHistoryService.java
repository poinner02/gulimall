package com.merchen.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.merchen.common.utils.PageUtils;
import com.merchen.gulimall.coupon.entity.CouponHistoryEntity;

import java.util.Map;

/**
 * 优惠券领取历史记录
 *
 * @author ChenZhongBo
 * @email 961208477@qq.com
 * @date 2022-06-05 18:08:59
 */
public interface CouponHistoryService extends IService<CouponHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

