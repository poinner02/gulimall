package com.merchen.gulimall.product.service.impl;

import com.merchen.gulimall.product.dao.CategoryBrandRelationDao;
import com.merchen.gulimall.product.entity.CategoryBrandRelationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.merchen.common.utils.PageUtils;
import com.merchen.common.utils.Query;

import com.merchen.gulimall.product.dao.BrandDao;
import com.merchen.gulimall.product.entity.BrandEntity;
import com.merchen.gulimall.product.service.BrandService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Autowired
    private CategoryBrandRelationDao categoryBrandRelationDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<BrandEntity> ipage = new Query<BrandEntity>().getPage(params);

        String key = (String) params.get("key");

        QueryWrapper<BrandEntity> wrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(key)){
            wrapper.eq("brand_id",key).or().like("name", key);
        }
        IPage<BrandEntity> page = baseMapper.selectPage(ipage, wrapper);
        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void updateDetails(BrandEntity brand) {
        baseMapper.updateById(brand);
        //更新关联表中brand数据
        String name = brand.getName();
        if(!StringUtils.isEmpty(name)){
            //sql : update xx set brand_name = ? where brand_id = ?
            CategoryBrandRelationEntity categoryBrandRelationEntity = new CategoryBrandRelationEntity();
            categoryBrandRelationEntity.setBrandName(brand.getName());
            QueryWrapper<CategoryBrandRelationEntity> wrapper = new QueryWrapper<>();
            wrapper.eq("brand_id", brand.getBrandId());
            categoryBrandRelationDao.update(categoryBrandRelationEntity,wrapper);
        }

    }

}