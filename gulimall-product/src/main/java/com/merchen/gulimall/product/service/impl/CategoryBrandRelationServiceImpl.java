package com.merchen.gulimall.product.service.impl;

import com.merchen.gulimall.product.entity.BrandEntity;
import com.merchen.gulimall.product.entity.CategoryEntity;
import com.merchen.gulimall.product.service.BrandService;
import com.merchen.gulimall.product.service.CategoryService;
import com.merchen.gulimall.product.vo.BrandVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.merchen.common.utils.PageUtils;
import com.merchen.common.utils.Query;

import com.merchen.gulimall.product.dao.CategoryBrandRelationDao;
import com.merchen.gulimall.product.entity.CategoryBrandRelationEntity;
import com.merchen.gulimall.product.service.CategoryBrandRelationService;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService brandService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id", params.get("brandId"))
        );

        return new PageUtils(page);
    }

    @Override
    public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
        Long catelogId = categoryBrandRelation.getCatelogId();
        CategoryEntity categoryEntity = categoryService.getById(catelogId);
        Long brandId = categoryBrandRelation.getBrandId();
        BrandEntity brandEntity = brandService.getById(brandId);
        categoryBrandRelation.setBrandName(brandEntity.getName());
        categoryBrandRelation.setCatelogName(categoryEntity.getName());
        baseMapper.insert(categoryBrandRelation);
    }

    @Override
    public void updateCateGoryDetails(Long catId, String name) {
        baseMapper.updateCateGoryDetails(catId,name);
    }

    @Override
    public List<BrandEntity> getList(Long catId) {

        QueryWrapper<CategoryBrandRelationEntity> wrapper = new QueryWrapper<>();

        if(!catId.equals(0L)){
            //inner 表是一个分类id对应多个品牌id
            wrapper.eq("catelog_id", catId);
        }
        //收集品牌id集合
        List<CategoryBrandRelationEntity> entityList = baseMapper.selectList(wrapper);

        List<Long> brandIds = entityList.stream().map((item) -> {
            return item.getBrandId();
        }).collect(Collectors.toList());
        //可能别的功能需要获取brand的信息
        Collection<BrandEntity> brandEntities = null;
        if(brandIds!=null && brandIds.size()>0){
             brandEntities = brandService.listByIds(brandIds);
        }
        return (List<BrandEntity>) brandEntities;
    }

}