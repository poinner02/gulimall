package com.merchen.gulimall.coupon.service.impl;

import com.merchen.common.to.MemberPrice;
import com.merchen.common.to.SkuReductionTO;
import com.merchen.gulimall.coupon.entity.MemberPriceEntity;
import com.merchen.gulimall.coupon.entity.SkuLadderEntity;
import com.merchen.gulimall.coupon.service.MemberPriceService;
import com.merchen.gulimall.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.merchen.common.utils.PageUtils;
import com.merchen.common.utils.Query;

import com.merchen.gulimall.coupon.dao.SkuFullReductionDao;
import com.merchen.gulimall.coupon.entity.SkuFullReductionEntity;
import com.merchen.gulimall.coupon.service.SkuFullReductionService;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    SkuLadderService skuLadderService;

    @Autowired
    MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveReduction(SkuReductionTO skuReductionTO) {
        //1.满减打折,会员价格
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        BeanUtils.copyProperties(skuReductionTO, skuLadderEntity);
        skuLadderEntity.setAddOther(skuReductionTO.getCountStatus());
        if(skuReductionTO.getFullCount()>0){
            skuLadderService.save(skuLadderEntity);
        }

        SkuFullReductionEntity  skuFullReductionEntity = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuReductionTO, skuFullReductionEntity);
        if(skuReductionTO.getFullPrice().compareTo(new BigDecimal("0"))==1){
            this.save(skuFullReductionEntity);
        }

        List<MemberPrice> memberPriceList = skuReductionTO.getMemberPrice();
        List<MemberPriceEntity> priceEntities = memberPriceList.stream().filter(entity->{
            return entity.getPrice().compareTo(new BigDecimal("0")) == 1;
        }).map((mem) -> {
            MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
            memberPriceEntity.setMemberPrice(mem.getPrice());
            memberPriceEntity.setSkuId(skuReductionTO.getSkuId());
            memberPriceEntity.setMemberLevelId(mem.getId());
            memberPriceEntity.setMemberLevelName(mem.getName());
            memberPriceEntity.setAddOther(1);
            return memberPriceEntity;
        }).collect(Collectors.toList());
        memberPriceService.saveBatch(priceEntities);
    }

}