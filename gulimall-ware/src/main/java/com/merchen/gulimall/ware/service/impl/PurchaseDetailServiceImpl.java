package com.merchen.gulimall.ware.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.merchen.common.utils.PageUtils;
import com.merchen.common.utils.Query;

import com.merchen.gulimall.ware.dao.PurchaseDetailDao;
import com.merchen.gulimall.ware.entity.PurchaseDetailEntity;
import com.merchen.gulimall.ware.service.PurchaseDetailService;
import org.springframework.util.StringUtils;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        //ttp://localhost:88/api/ware/purchasedetail/list?t=1656398246917&page=1&limit=10&key=q&status=0&wareId=1
//        IPage<PurchaseDetailEntity> page = this.page(
//                new Query<PurchaseDetailEntity>().getPage(params),
//                new QueryWrapper<PurchaseDetailEntity>()
//        );
        IPage<PurchaseDetailEntity> page = new Query<PurchaseDetailEntity>().getPage(params);
        QueryWrapper<PurchaseDetailEntity> wrapper = new QueryWrapper<>();
        String key = (String)params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.and(object->{
                object.eq("id", key)
                        .or()
                        .eq("sku_num", key);
            });
        }
        String status = (String)params.get("status");
        if(!StringUtils.isEmpty(status)){
            wrapper.eq("status", status);
        }
        String wareId = (String)params.get("wareId");
        if(!StringUtils.isEmpty(wareId)){
            wrapper.eq("ware_id", wareId);
        }
        IPage<PurchaseDetailEntity> entityIPage = this.baseMapper.selectPage(page, wrapper);
        return new PageUtils(entityIPage);
    }

    @Override
    public List<PurchaseDetailEntity> listDetailByPurchaseId(Long id) {
        List<PurchaseDetailEntity> entityList = this.baseMapper.selectList(new QueryWrapper<PurchaseDetailEntity>().eq("purchase_id", id));
        return entityList;
    }

}