package com.merchen.gulimall.ware.service.impl;

import com.merchen.common.constant.WareConstant;
import com.merchen.gulimall.ware.entity.PurchaseDetailEntity;
import com.merchen.gulimall.ware.feign.ProductFeignService;
import com.merchen.gulimall.ware.service.PurchaseDetailService;
import com.merchen.gulimall.ware.service.WareSkuService;
import com.merchen.gulimall.ware.vo.MergePurchaseVO;
import com.merchen.gulimall.ware.vo.PurchaseDoneVO;
import com.merchen.gulimall.ware.vo.PurchaseReasonVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.merchen.common.utils.PageUtils;
import com.merchen.common.utils.Query;

import com.merchen.gulimall.ware.dao.PurchaseDao;
import com.merchen.gulimall.ware.entity.PurchaseEntity;
import com.merchen.gulimall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {


    @Autowired
    private PurchaseDetailService purchaseDetailService;


    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryUnreceivelist(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>().eq("status", WareConstant.PurChaseStatusEnum.CREATED.getCode()).or().eq("status", WareConstant.PurChaseStatusEnum.ASSIGN.getCode())
        );
        return new PageUtils(page);
    }

    @Override
    public void merge(MergePurchaseVO mergePurchaseVO) {
        List<Long> items = mergePurchaseVO.getItems();
        Long purchaseId = mergePurchaseVO.getPurchaseId();
        if (purchaseId == null) {
            //没有分配的采购,新建一个
            PurchaseEntity purchaseEntity = new PurchaseEntity();
//            purchaseEntity.setCreateTime(new Date());
//            purchaseEntity.setUpdateTime(new Date());
//            purchaseEntity.setPriority(1);
            /**
             * 新建
             * 已分配
             * 已领取
             * 已完成
             * 有异常
             */
            purchaseEntity.setStatus(WareConstant.PurChaseStatusEnum.CREATED.getCode());
            this.baseMapper.insert(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        }
        Long finalPurchaseId = purchaseId;
        //修改采购项的状态
        List<PurchaseDetailEntity> entityList = items.stream().filter(id -> {
            //确认采购项是否是新建和以分配才可以继续操作
            PurchaseDetailEntity byId = purchaseDetailService.getById(id);
            return byId.getStatus() <= WareConstant.PurChaseDetailStatusEnum.ASSIGN.getCode();
        }).map(id -> {
            //更新个别字段
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            purchaseDetailEntity.setId(id);
            purchaseDetailEntity.setPurchaseId(finalPurchaseId);
            purchaseDetailEntity.setStatus(WareConstant.PurChaseDetailStatusEnum.ASSIGN.getCode());
            return purchaseDetailEntity;
        }).collect(Collectors.toList());
        if (entityList != null && entityList.size() > 0) {
            purchaseDetailService.updateBatchById(entityList);
            //跟新采购单的时间
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setId(finalPurchaseId);
            purchaseEntity.setUpdateTime(new Date());
            this.baseMapper.updateById(purchaseEntity);
        }


    }

    @Override
    public void receive(List<Long> ids) {
        //1 确认采购单的状态
        List<PurchaseEntity> purchaseEntities = ids.stream().map((id) -> {
            PurchaseEntity purchaseEntity = this.baseMapper.selectById(id);
            return purchaseEntity;
        }).filter((item) -> {
            //过滤得到新建和分配
            Integer status = item.getStatus();
            if (status == WareConstant.PurChaseStatusEnum.ASSIGN.getCode() || status == WareConstant.PurChaseStatusEnum.CREATED.getCode()) {
                return true;
            }
            return false;
        }).map(item -> {
            //只更新status 字段
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setId(item.getId());
            purchaseEntity.setStatus(WareConstant.PurChaseStatusEnum.RECEIVE.getCode());
            return purchaseEntity;
        }).collect(Collectors.toList());
        //2 改变采购单的状态
        this.updateBatchById(purchaseEntities);
        // 3 改变采购项的状态
        purchaseEntities.forEach(item -> {
            List<PurchaseDetailEntity> entityList = purchaseDetailService.listDetailByPurchaseId(item.getId());
            List<PurchaseDetailEntity> collect = entityList.stream().map(entity -> {
                //更新status字段
                PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
                purchaseDetailEntity.setId(entity.getId());
                purchaseDetailEntity.setStatus(WareConstant.PurChaseDetailStatusEnum.DOING.getCode());
                return purchaseDetailEntity;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(collect);
        });
    }

    /**
     * 用户端app操作接口
     *
     * @param purchaseDoneVO
     */
    @Transactional
    @Override
    public void done(PurchaseDoneVO purchaseDoneVO) {

        //判断采购单是否更新状态成功的标志位
        Boolean flag = true;
        // 改变采购项的状态
        List<PurchaseReasonVO> items = purchaseDoneVO.getItems();
        List<PurchaseDetailEntity> list = new ArrayList<>();
        //operator is update status
        for (PurchaseReasonVO item : items) {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            if (item.getStatus() == WareConstant.PurChaseDetailStatusEnum.DONEFAILED.getCode()) {
                //采购失败
                flag = false;
                purchaseDetailEntity.setStatus(item.getStatus());
            } else {
                purchaseDetailEntity.setStatus(WareConstant.PurChaseDetailStatusEnum.FINISH.getCode());
                //存入库存
                //todo
                //采购成功录入库存
                Long itemId = item.getItemId();
                PurchaseDetailEntity entity = purchaseDetailService.getById(itemId);
                wareSkuService.addStock(entity.getSkuId(),entity.getWareId(),entity.getSkuNum());
            }
            purchaseDetailEntity.setId(item.getItemId());
            list.add(purchaseDetailEntity);
        }
        purchaseDetailService.updateBatchById(list);
        // 改变采购单的状态
        Long id = purchaseDoneVO.getId();
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setStatus(flag ? WareConstant.PurChaseStatusEnum.FINISH.getCode() : WareConstant.PurChaseStatusEnum.HASERROR.getCode());
        purchaseEntity.setId(id);
        this.baseMapper.updateById(purchaseEntity);


    }

}