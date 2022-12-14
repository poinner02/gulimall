package com.merchen.gulimall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.merchen.common.exception.LockStockException;
import com.merchen.common.to.ProductSkuInfoTO;
import com.merchen.common.to.SkuHasStockTO;
import com.merchen.common.to.mq.OrderEntityTo;
import com.merchen.common.to.mq.StockDetailTo;
import com.merchen.common.to.mq.StockLockTo;
import com.merchen.common.utils.R;

import com.merchen.gulimall.ware.entity.WareOrderTaskDetailEntity;
import com.merchen.gulimall.ware.entity.WareOrderTaskEntity;
import com.merchen.gulimall.ware.feign.OrderFeignService;
import com.merchen.gulimall.ware.feign.ProductFeignService;
import com.merchen.gulimall.ware.service.WareOrderTaskDetailService;
import com.merchen.gulimall.ware.service.WareOrderTaskService;
import com.merchen.gulimall.ware.vo.OrderTo;
import com.merchen.gulimall.ware.vo.OrderItemEntity;
import com.merchen.gulimall.ware.vo.WareSkuLockVo;
import lombok.Data;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.merchen.common.utils.PageUtils;
import com.merchen.common.utils.Query;

import com.merchen.gulimall.ware.dao.WareSkuDao;
import com.merchen.gulimall.ware.entity.WareSkuEntity;
import com.merchen.gulimall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {
    @Autowired
    private WareSkuDao wareSkuDao;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private WareOrderTaskService wareOrderTaskService;

    @Autowired
    private WareOrderTaskDetailService wareOrderTaskDetailService;


    @Autowired
    private OrderFeignService orderFeignService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
//        IPage<WareSkuEntity> page = this.page(
//                new Query<WareSkuEntity>().getPage(params),
//                new QueryWrapper<WareSkuEntity>()
//        );

        /**
         *  wareId: 123,//??????id
         *    skuId: 123//??????id
         */

        IPage<WareSkuEntity> page = new Query<WareSkuEntity>().getPage(params);
        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.and(object -> {
                object.eq("id", key)
                        .or()
                        .eq("name", key);
            });
        }
        String wareId = (String) params.get("wareId");
        if (!StringUtils.isEmpty(wareId)) {
            wrapper.eq("ware_id", wareId);
        }
        String skuId = (String) params.get("skuId");
        if (!StringUtils.isEmpty(skuId)) {
            wrapper.eq("sku_id", skuId);
        }
        IPage<WareSkuEntity> wareSkuEntityIPage = this.baseMapper.selectPage(page, wrapper);
        return new PageUtils(wareSkuEntityIPage);

    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {

        //??????????????????insert
        Integer count = this.baseMapper.selectCount(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if (count == 0) {
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);
            //todo ????????????????????????????????? ???try ????????????
            try {
                R r = productFeignService.RemoteInfo(skuId);
                Integer code = (Integer) r.get("code");
                if (code == 0) {
                    //????????????
                    ProductSkuInfoTO skuInfoTO = (ProductSkuInfoTO) r.get("skuInfoTO");
                    String skuName = skuInfoTO.getSkuName();
                    wareSkuEntity.setSkuName(skuName);
                }
            } catch (Exception e) {

            }
            //todo ?????????????????????
            this.baseMapper.insert(wareSkuEntity);
        } else {
            //??????????????????????????????
            wareSkuDao.addStock(skuId, wareId, skuNum);
        }
    }

    @Override
    public WareSkuEntity getOneBySkuId(Long id) {
        WareSkuEntity wareSkuEntity = this.baseMapper.selectOne(new QueryWrapper<WareSkuEntity>().eq("sku_id", id));
        return wareSkuEntity;
    }

    @Override
    public List<SkuHasStockTO> hasStock(List<Long> skuIds) {

        return baseMapper.hasStock(skuIds);
    }


    /**
     * ???????????????
     * 1??????
     * 2??????
     *
     * @param wareSkuLockVo
     * @return
     */
    //todo ?????????bug??????????????????????????????????????????????????????????????????
    @Transactional(rollbackFor = LockStockException.class)
    //???????????????
    @Override
    public Boolean orderLockStock(WareSkuLockVo wareSkuLockVo) {
        //????????????????????????
        WareOrderTaskEntity wareOrderTaskEntity = new WareOrderTaskEntity();
        wareOrderTaskEntity.setCreateTime(new Date());
        wareOrderTaskEntity.setTaskStatus(0);//0??????????????????1????????????
        wareOrderTaskEntity.setOrderSn(wareSkuLockVo.getOrderSn());
        wareOrderTaskService.save(wareOrderTaskEntity);

        List<OrderItemEntity> locks = wareSkuLockVo.getLocks();
        //??????????????????id?????????skuid???????????????
        List<WareIdHasSkuStock> collect = locks.stream().map(item -> {
            WareIdHasSkuStock wareIdHasSkuStock = new WareIdHasSkuStock();
            wareIdHasSkuStock.setNum(item.getSkuQuantity());
            wareIdHasSkuStock.setSkuId(item.getSkuId());
            List<Long> wareIds = baseMapper.wareIdHasSkuStock(item.getSkuId());
            wareIdHasSkuStock.setWareId(wareIds);
            return wareIdHasSkuStock;
        }).collect(Collectors.toList());
        //??????????????????????????????????????????
        for (WareIdHasSkuStock wareIdHasSkuStock : collect) {
            Boolean lock = false;
            Long skuId = wareIdHasSkuStock.getSkuId();
            List<Long> wareIds = wareIdHasSkuStock.getWareId();
            String msg = "??????????????????????????????" + skuId + "??????????????????????????????,?????????" + "com.merchen.gulimall.ware.exception.LockStockException";
            if (wareIds == null || wareIds.size() == 0) {
                throw new LockStockException(msg);
            }
            //????????????sku???sku?????????????????????id
            // 1-2-1 2-1-3 3-1-4(x) ???????????????????????????
            for (Long wareId : wareIds) {
                //?????????????????????sku??????????????????????????????????????????????????????
                Integer count = wareSkuDao.lockSkuStock(skuId, wareId, wareIdHasSkuStock.getNum());
                if (count == 1) {
                    lock = true;
                    WareOrderTaskDetailEntity wareOrderTaskDetailEntity = new WareOrderTaskDetailEntity(null, skuId,
                            null,
                            wareIdHasSkuStock.getNum(),
                            wareOrderTaskEntity.getId(),
                            wareId,
                            1);
                    wareOrderTaskDetailService.save(wareOrderTaskDetailEntity);
                    //?????????????????????????????????????????????mq
                    //???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????id???????????????????????????????????????????????????
                    StockLockTo stockLockTo = new StockLockTo();
                    stockLockTo.setId(wareOrderTaskEntity.getId());
                    StockDetailTo detailTo = new StockDetailTo();
                    BeanUtils.copyProperties(wareOrderTaskDetailEntity, detailTo);
                    stockLockTo.setDetail(detailTo);
                    try {
                        rabbitTemplate.convertAndSend("stock-event-exchange", "stock.locked", stockLockTo);
                    } catch (Exception e) {
                        //????????????
                        Integer step = 10;
                        while (step-- > 0) {
                            rabbitTemplate.convertAndSend("stock-event-exchange", "stock.locked", stockLockTo);
                        }
                    }
                    break;
                } else {
                    //?????????????????????id
                }
            }
            //??????????????????
            //?????????????????????id?????????skuid??????????????????????????????
            if (!lock) {

                throw new LockStockException(msg);
            }
        }
        return true;
    }

    //????????????????????????
    @Override
    public void unLockStock(StockLockTo stockLockTo) {

        //??????
        // stockLockTo???null ,???????????????????????????????????????????????????
        // stockLockTo??????null,??????id???
        //              ??????????????????????????????db????????????????????????????????????????????????????????????
        //              ??????db?????????????????????
        //                      ?????????????????????order???????????????????????????
        //                      ?????????????????????order???????????????????????????
        if (stockLockTo != null) {
            //?????????????????????
            StockDetailTo mqDetail = stockLockTo.getDetail();
            //??????????????????by ID
            Long id = mqDetail.getId();
            WareOrderTaskDetailEntity dbEntity = wareOrderTaskDetailService.getById(id);
            if (dbEntity == null) {
                //????????????????????????????????????
                //mq handle ack
            } else {
                //????????????
                Long lockToId = stockLockTo.getId();
                WareOrderTaskEntity byId1 = wareOrderTaskService.getById(lockToId);
                R r = orderFeignService.getStatus(byId1.getOrderSn());
                //?????????????????????
                //?????????????????????order???????????????????????????
                //?????????????????????order???????????????????????????
                if (r.getCode() == 0) {
                    OrderTo order = r.getData("data", new TypeReference<OrderTo>() {
                    });
                    //???????????????????????????????????????????????????
                    if (order == null || order.getStatus() == 4) {
                        //??????????????????????????????
                        if (dbEntity.getLockStatus() == 1) {
                            //??????
                            unLockWare(mqDetail);
                        }
                    } else {
                        //???????????????????????????,????????????
                    }
                } else {
                    //?????????????????????????????????????????????????????????,????????????????????????
                    throw new RuntimeException("??????????????????");
                }
            }

        } else {
            //tockLockTo???null ,???????????????????????????????????????????????????
        }

    }

    /**
     * ???????????????????????????????????????????????????-??????????????????????????????????????????????????????????????????-???????????????????????????
     * ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????-?????????????????????-?????????????????????
     * ??????:
     * ???????????????????????????mq??????????????????mq?????????OrderEntity???
     * 1>??????????????????????????????
     * 1??????????????????id
     * 2?????????????????????id?????????????????????????????????
     * 3?????????????????????
     *
     * @param orderTo
     */
    @Transactional
    @Override
    public void unLockStock(OrderEntityTo orderTo) {
        String orderSn = orderTo.getOrderSn();
        WareOrderTaskEntity task = wareOrderTaskService.getWareTask(orderSn);
        Long taskId = task.getId();
        List<WareOrderTaskDetailEntity> list = wareOrderTaskDetailService.list(new QueryWrapper<WareOrderTaskDetailEntity>().eq("task_id", taskId).eq("lock_status", 1));
        for (WareOrderTaskDetailEntity wareOrderTaskDetailEntity : list) {
            StockDetailTo stockDetailTo = new StockDetailTo();
            BeanUtils.copyProperties(wareOrderTaskDetailEntity, stockDetailTo);
            unLockWare(stockDetailTo);
        }
    }

    private void unLockWare(StockDetailTo mqDetail) {
        wareSkuDao.modifyWare(mqDetail.getSkuId(), mqDetail.getWareId(), mqDetail.getSkuNum());
        WareOrderTaskDetailEntity wareOrderTaskDetailEntity = new WareOrderTaskDetailEntity();
        wareOrderTaskDetailEntity.setId(mqDetail.getId());
        wareOrderTaskDetailEntity.setLockStatus(2);
        //??????????????????????????????
        wareOrderTaskDetailService.updateById(wareOrderTaskDetailEntity);
        //??????????????????

    }

    @Data
    class WareIdHasSkuStock {
        private Long skuId;
        private Integer num;//??????
        private List<Long> wareId;
    }
}