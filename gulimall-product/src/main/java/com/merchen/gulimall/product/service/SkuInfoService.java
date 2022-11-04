package com.merchen.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.merchen.common.utils.PageUtils;
import com.merchen.gulimall.product.entity.SkuInfoEntity;
import com.merchen.gulimall.product.vo.SkuItemVo;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * sku信息
 *
 * @author ChenZhongBo
 * @email 961208477@qq.com
 * @date 2022-06-04 20:05:18
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuInfo(SkuInfoEntity skuInfoEntity);

    PageUtils queryPageOnCondiction(Map<String, Object> params);

    SkuItemVo item(Long skuId) throws ExecutionException, InterruptedException;
}

