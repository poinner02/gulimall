package com.merchen.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.merchen.common.utils.PageUtils;
import com.merchen.gulimall.product.entity.SpuInfoEntity;
import com.merchen.gulimall.product.vo.SpuSaveVO;

import java.util.Map;

/**
 * spu信息
 *
 * @author ChenZhongBo
 * @email 961208477@qq.com
 * @date 2022-06-04 20:05:18
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfo(SpuSaveVO saveVO);

    PageUtils queryPageOnCondiction(Map<String, Object> params);

    void upProduct(Long spuId);
}

