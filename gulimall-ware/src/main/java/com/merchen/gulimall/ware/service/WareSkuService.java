package com.merchen.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.merchen.common.to.SkuHasStockTO;
import com.merchen.common.to.mq.OrderEntityTo;
import com.merchen.common.to.mq.StockLockTo;
import com.merchen.common.utils.PageUtils;
import com.merchen.gulimall.ware.entity.WareSkuEntity;
import com.merchen.gulimall.ware.vo.WareSkuLockVo;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author ChenZhongBo
 * @email 961208477@qq.com
 * @date 2022-06-05 19:23:27
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);

    WareSkuEntity getOneBySkuId(Long id);

    List<SkuHasStockTO> hasStock(List<Long> skuIds);

    Boolean orderLockStock(WareSkuLockVo wareSkuLockVo);

    void unLockStock(StockLockTo stockLockTo);

    void unLockStock(OrderEntityTo orderTo);

}

