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
            //?????????????????????,????????????
            PurchaseEntity purchaseEntity = new PurchaseEntity();
//            purchaseEntity.setCreateTime(new Date());
//            purchaseEntity.setUpdateTime(new Date());
//            purchaseEntity.setPriority(1);
            /**
             * ??????
             * ?????????
             * ?????????
             * ?????????
             * ?????????
             */
            purchaseEntity.setStatus(WareConstant.PurChaseStatusEnum.CREATED.getCode());
            this.baseMapper.insert(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        }
        Long finalPurchaseId = purchaseId;
        //????????????????????????
        List<PurchaseDetailEntity> entityList = items.stream().filter(id -> {
            //???????????????????????????????????????????????????????????????
            PurchaseDetailEntity byId = purchaseDetailService.getById(id);
            return byId.getStatus() <= WareConstant.PurChaseDetailStatusEnum.ASSIGN.getCode();
        }).map(id -> {
            //??????????????????
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            purchaseDetailEntity.setId(id);
            purchaseDetailEntity.setPurchaseId(finalPurchaseId);
            purchaseDetailEntity.setStatus(WareConstant.PurChaseDetailStatusEnum.ASSIGN.getCode());
            return purchaseDetailEntity;
        }).collect(Collectors.toList());
        if (entityList != null && entityList.size() > 0) {
            purchaseDetailService.updateBatchById(entityList);
            //????????????????????????
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setId(finalPurchaseId);
            purchaseEntity.setUpdateTime(new Date());
            this.baseMapper.updateById(purchaseEntity);
        }


    }

    @Override
    public void receive(List<Long> ids) {
        //1 ????????????????????????
        List<PurchaseEntity> purchaseEntities = ids.stream().map((id) -> {
            PurchaseEntity purchaseEntity = this.baseMapper.selectById(id);
            return purchaseEntity;
        }).filter((item) -> {
            //???????????????????????????
            Integer status = item.getStatus();
            if (status == WareConstant.PurChaseStatusEnum.ASSIGN.getCode() || status == WareConstant.PurChaseStatusEnum.CREATED.getCode()) {
                return true;
            }
            return false;
        }).map(item -> {
            //?????????status ??????
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setId(item.getId());
            purchaseEntity.setStatus(WareConstant.PurChaseStatusEnum.RECEIVE.getCode());
            return purchaseEntity;
        }).collect(Collectors.toList());
        //2 ????????????????????????
        this.updateBatchById(purchaseEntities);
        // 3 ????????????????????????
        purchaseEntities.forEach(item -> {
            List<PurchaseDetailEntity> entityList = purchaseDetailService.listDetailByPurchaseId(item.getId());
            List<PurchaseDetailEntity> collect = entityList.stream().map(entity -> {
                //??????status??????
                PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
                purchaseDetailEntity.setId(entity.getId());
                purchaseDetailEntity.setStatus(WareConstant.PurChaseDetailStatusEnum.DOING.getCode());
                return purchaseDetailEntity;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(collect);
        });
    }

    /**
     * ?????????app????????????
     *
     * @param purchaseDoneVO
     */
    @Transactional
    @Override
    public void done(PurchaseDoneVO purchaseDoneVO) {

        //???????????????????????????????????????????????????
        Boolean flag = true;
        // ????????????????????????
        List<PurchaseReasonVO> items = purchaseDoneVO.getItems();
        List<PurchaseDetailEntity> list = new ArrayList<>();
        //operator is update status
        for (PurchaseReasonVO item : items) {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            if (item.getStatus() == WareConstant.PurChaseDetailStatusEnum.DONEFAILED.getCode()) {
                //????????????
                flag = false;
                purchaseDetailEntity.setStatus(item.getStatus());
            } else {
                purchaseDetailEntity.setStatus(WareConstant.PurChaseDetailStatusEnum.FINISH.getCode());
                //????????????
                //todo
                //????????????????????????
                Long itemId = item.getItemId();
                PurchaseDetailEntity entity = purchaseDetailService.getById(itemId);
                wareSkuService.addStock(entity.getSkuId(),entity.getWareId(),entity.getSkuNum());
            }
            purchaseDetailEntity.setId(item.getItemId());
            list.add(purchaseDetailEntity);
        }
        purchaseDetailService.updateBatchById(list);
        // ????????????????????????
        Long id = purchaseDoneVO.getId();
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setStatus(flag ? WareConstant.PurChaseStatusEnum.FINISH.getCode() : WareConstant.PurChaseStatusEnum.HASERROR.getCode());
        purchaseEntity.setId(id);
        this.baseMapper.updateById(purchaseEntity);


    }

}