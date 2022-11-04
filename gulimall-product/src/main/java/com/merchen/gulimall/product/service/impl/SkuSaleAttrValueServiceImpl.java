package com.merchen.gulimall.product.service.impl;

import com.merchen.gulimall.product.vo.SkuItemVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.merchen.common.utils.PageUtils;
import com.merchen.common.utils.Query;

import com.merchen.gulimall.product.dao.SkuSaleAttrValueDao;
import com.merchen.gulimall.product.entity.SkuSaleAttrValueEntity;
import com.merchen.gulimall.product.service.SkuSaleAttrValueService;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuItemVo.ItemSaleItemAttrsVo> getSaleAttrsBySpuId(Long spuId) {
        List<SkuItemVo.ItemSaleItemAttrsVo> attrsVos = baseMapper.getSaleAttrsBySpuId(spuId);
        return attrsVos;
    }

    @Override
    public List<String> getCartAttrs(Long skuId) {
        List<String> list = this.baseMapper.getCartAttrs(skuId);
        return list;
    }

}