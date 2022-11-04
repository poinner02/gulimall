package com.merchen.gulimall.product.service.impl;

import com.merchen.gulimall.product.entity.AttrEntity;
import com.merchen.gulimall.product.service.AttrService;
import com.merchen.gulimall.product.vo.AttrVO;
import com.merchen.gulimall.product.vo.BaseAttrs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.merchen.common.utils.PageUtils;
import com.merchen.common.utils.Query;

import com.merchen.gulimall.product.dao.ProductAttrValueDao;
import com.merchen.gulimall.product.entity.ProductAttrValueEntity;
import com.merchen.gulimall.product.service.ProductAttrValueService;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

    @Autowired
    private AttrService attrService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveBaseAttr(List<BaseAttrs> baseAttrs,Long spuId) {
        if (baseAttrs == null || baseAttrs.size() == 0) {
            return;
        } else {
            List<ProductAttrValueEntity> productAttrValueEntityList = baseAttrs.stream().map((item) -> {
                ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
                productAttrValueEntity.setAttrValue(item.getAttrValues());
                productAttrValueEntity.setAttrId(item.getAttrId());
                //查询attrService获取名字
                AttrEntity attrEntity = attrService.getById(item.getAttrId());
                productAttrValueEntity.setAttrName(attrEntity.getAttrName());
                //是否快速展示
                productAttrValueEntity.setQuickShow(item.getShowDesc());
                productAttrValueEntity.setSpuId(spuId);
                return productAttrValueEntity;
            }).collect(Collectors.toList());
            this.saveBatch(productAttrValueEntityList);
        }

    }

    @Override
    public List<ProductAttrValueEntity> getList(Long spuId) {
        List<ProductAttrValueEntity> list = this.baseMapper.selectList(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
        return list;
    }

}