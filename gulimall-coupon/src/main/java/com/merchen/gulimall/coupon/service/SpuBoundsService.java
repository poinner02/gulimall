package com.merchen.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.merchen.common.utils.PageUtils;
import com.merchen.gulimall.coupon.entity.SpuBoundsEntity;

import java.util.Map;

/**
 * 商品spu积分设置
 *
 * @author ChenZhongBo
 * @email 961208477@qq.com
 * @date 2022-06-05 18:08:58
 */
public interface SpuBoundsService extends IService<SpuBoundsEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

