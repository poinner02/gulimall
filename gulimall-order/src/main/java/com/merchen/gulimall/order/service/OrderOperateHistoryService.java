package com.merchen.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.merchen.common.utils.PageUtils;
import com.merchen.gulimall.order.entity.OrderOperateHistoryEntity;

import java.util.Map;

/**
 * 订单操作历史记录
 *
 * @author ChenZhongBo
 * @email 961208477@qq.com
 * @date 2022-06-05 19:20:45
 */
public interface OrderOperateHistoryService extends IService<OrderOperateHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

