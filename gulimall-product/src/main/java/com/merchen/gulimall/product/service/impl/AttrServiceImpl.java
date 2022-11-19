package com.merchen.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.merchen.common.constant.ProductConstant;
import com.merchen.gulimall.product.entity.*;
import com.merchen.gulimall.product.service.*;
import com.merchen.gulimall.product.vo.AttrRespVO;
import com.merchen.gulimall.product.vo.AttrVO;
import com.merchen.gulimall.product.vo.SpuAttrVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.merchen.common.utils.PageUtils;
import com.merchen.common.utils.Query;

import com.merchen.gulimall.product.dao.AttrDao;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long cateLogId, String type) {

        IPage<AttrEntity> ipage = new Query<AttrEntity>().getPage(params);
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.and((object) -> {
                object.eq("attr_id", key).or().like("attr_name", key);
            });
        }
        //区分销售属性和规格属性
        wrapper.eq("attr_type", "base".equalsIgnoreCase(type) ? 1 : 0);
        IPage<AttrEntity> attrEntityIPage = null;
        if (cateLogId.equals(0L)) {
            //查询全部
            attrEntityIPage = this.baseMapper.selectPage(ipage, wrapper);
        } else {
            //精准查询
            wrapper.eq("catelog_id", cateLogId);
            attrEntityIPage = this.baseMapper.selectPage(ipage, wrapper);
        }
        PageUtils pageUtils = new PageUtils(attrEntityIPage);
        List<AttrEntity> records = attrEntityIPage.getRecords();
        List<AttrRespVO> attrRespVOS = records.stream().map((attrEntity -> {
            AttrRespVO attrRespVO = new AttrRespVO();
            BeanUtils.copyProperties(attrEntity, attrRespVO);
            //设置分组和分类名字
            Long catelogId = attrEntity.getCatelogId();
            CategoryEntity categoryEntity = categoryService.getById(catelogId);
            if (categoryEntity != null) {
                attrRespVO.setCatelogName(categoryEntity.getName());
            }
            //规格属性做分组回显
            if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BAES.getCode()) {
                Long attrId = attrEntity.getAttrId();
                AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationService.getOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
                if (relationEntity != null) {
                    //可选字段
                    Long attrGroupId = relationEntity.getAttrGroupId();
                    if(attrGroupId!=null){
                        AttrGroupEntity groupEntity = attrGroupService.getById(attrGroupId);
                        attrRespVO.setGroupName(groupEntity.getAttrGroupName());
                    }
                }
            }
            return attrRespVO;
        })).collect(Collectors.toList());

        pageUtils.setList(attrRespVOS);
        return pageUtils;
    }

    @Override
    public AttrVO getDetail(Long attrId) {

        AttrEntity attrEntity = this.getById(attrId);
        AttrVO arrtVO = new AttrVO();
        BeanUtils.copyProperties(attrEntity, arrtVO);
        Long catelogId = attrEntity.getCatelogId();
        //获取分类路径
        Long[] categoryPath = categoryService.getCategoryPath(catelogId);
        Collections.reverse(Arrays.asList(categoryPath));
        arrtVO.setCatelogPath(categoryPath);
        //规格属性
        if (arrtVO.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BAES.getCode()) {
            //获取分组路径
            AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationService.getOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
            if(relationEntity!=null){
                arrtVO.setAttrGroupId(relationEntity.getAttrGroupId());
            }


        }
        return arrtVO;
    }

    @Transactional
    @Override
    public void saveDetail(AttrVO attr) {


        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        baseMapper.insert(attrEntity);
        //规格属性做关联
        if (attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BAES.getCode()) {
            if(attr.getAttrGroupId()!=null){
                AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
                attrAttrgroupRelationEntity.setAttrId(attrEntity.getAttrId());
                attrAttrgroupRelationEntity.setAttrGroupId(attr.getAttrGroupId());
                attrAttrgroupRelationService.save(attrAttrgroupRelationEntity);
            }
        }
    }

    @Transactional
    @Override
    public void updateDetails(AttrVO attrVO) {

        //修改本属性表
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrVO, attrEntity);
        baseMapper.updateById(attrEntity);

        //销售属性不关联分组
        if (attrVO.getAttrType() != ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode()) {

            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrGroupId(attrVO.getAttrGroupId());
            attrAttrgroupRelationEntity.setAttrId(attrVO.getAttrId());
            UpdateWrapper<AttrAttrgroupRelationEntity> wrapper = new UpdateWrapper<>();
            wrapper.eq("attr_id", attrVO.getAttrId());
            if (attrAttrgroupRelationService.count(wrapper) > 0) {
                //修改关联表
                attrAttrgroupRelationService.update(attrAttrgroupRelationEntity, wrapper);
            } else {
                //表中没数据，新增
                attrAttrgroupRelationService.save(attrAttrgroupRelationEntity);
            }
        }

        //修改关联商品规格表
        //修复bug 查询商品销售属性 某个属性  其中某个属性名称是有多个值的
//        老代码查询结果是多个 ProductAttrValueEntity entity = productAttrValueService.getOne(new QueryWrapper<ProductAttrValueEntity>().eq("attr_id", attrVO.getAttrId()));
        List<ProductAttrValueEntity> productAttrValueEntityList = productAttrValueService.list(new QueryWrapper<ProductAttrValueEntity>().eq("attr_id", attrVO.getAttrId()));
        if(productAttrValueEntityList == null || productAttrValueEntityList.size() == 0){
            //do noting
        }else{
            //修改
            String attrName = attrVO.getAttrName();
            ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
            productAttrValueEntity.setAttrName(attrName);
            productAttrValueService.update(productAttrValueEntity,new QueryWrapper<ProductAttrValueEntity>().eq("attr_id", attrVO.getAttrId()));
        }
    }

    @Transactional
    @Override
    public List<AttrEntity> getRelationAttr(Long attrGroupId) {
        //通过中间表 关系表
        List<AttrAttrgroupRelationEntity> relationEntities = attrAttrgroupRelationService.list(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrGroupId));
        //收集与当前分组有关的attrid
        List<Long> attrIds = relationEntities.stream().map((item) -> {
            return item.getAttrId();
        }).collect(Collectors.toList());

        if(attrIds.size()==0 || attrIds ==null){
            return null;

        }
        Collection<AttrEntity> attrEntities = attrService.listByIds(attrIds);
        return (List<AttrEntity>) attrEntities;
    }

    @Transactional
    @Override
    public void updateSpuAttr(List<SpuAttrVO> spuAttrVOS, Long spuId) {
        //删除表中数据
        productAttrValueService.remove(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
        //后新增
        List<ProductAttrValueEntity> collect = spuAttrVOS.stream().map(item -> {
            ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
            BeanUtils.copyProperties(item, productAttrValueEntity);
            productAttrValueEntity.setSpuId(spuId);
            return productAttrValueEntity;
        }).collect(Collectors.toList());
        productAttrValueService.saveBatch(collect);
    }

    @Override
    public List<Long> getFilerIds(List<Long> nofilerIds) {

        return baseMapper.getFilerIds(nofilerIds);
    }
}