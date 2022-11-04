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
         *  wareId: 123,//仓库id
         *    skuId: 123//商品id
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

        //表中没数据则insert
        Integer count = this.baseMapper.selectCount(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if (count == 0) {
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);
            //todo 查询出错，不用事务回滚 用try 捕获异常
            try {
                R r = productFeignService.RemoteInfo(skuId);
                Integer code = (Integer) r.get("code");
                if (code == 0) {
                    //查询成功
                    ProductSkuInfoTO skuInfoTO = (ProductSkuInfoTO) r.get("skuInfoTO");
                    String skuName = skuInfoTO.getSkuName();
                    wareSkuEntity.setSkuName(skuName);
                }
            } catch (Exception e) {

            }
            //todo 分布式高级部分
            this.baseMapper.insert(wareSkuEntity);
        } else {
            //表中有数据则更新库存
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
     * 分析业务：
     * 1）：
     * 2）：
     *
     * @param wareSkuLockVo
     * @return
     */
    //todo 这里有bug问题，当其中任何一个商品锁定库存失败全部回滚
    @Transactional(rollbackFor = LockStockException.class)
    //锁库存服务
    @Override
    public Boolean orderLockStock(WareSkuLockVo wareSkuLockVo) {
        //库存任务实体对象
        WareOrderTaskEntity wareOrderTaskEntity = new WareOrderTaskEntity();
        wareOrderTaskEntity.setCreateTime(new Date());
        wareOrderTaskEntity.setTaskStatus(0);//0任务未结束，1任务结束
        wareOrderTaskEntity.setOrderSn(wareSkuLockVo.getOrderSn());
        wareOrderTaskService.save(wareOrderTaskEntity);

        List<OrderItemEntity> locks = wareSkuLockVo.getLocks();
        //获取哪些仓库id有当前skuid有库存信息
        List<WareIdHasSkuStock> collect = locks.stream().map(item -> {
            WareIdHasSkuStock wareIdHasSkuStock = new WareIdHasSkuStock();
            wareIdHasSkuStock.setNum(item.getSkuQuantity());
            wareIdHasSkuStock.setSkuId(item.getSkuId());
            List<Long> wareIds = baseMapper.wareIdHasSkuStock(item.getSkuId());
            wareIdHasSkuStock.setWareId(wareIds);
            return wareIdHasSkuStock;
        }).collect(Collectors.toList());
        //遍历有库存的，依次去扣库存。
        for (WareIdHasSkuStock wareIdHasSkuStock : collect) {
            Boolean lock = false;
            Long skuId = wareIdHasSkuStock.getSkuId();
            List<Long> wareIds = wareIdHasSkuStock.getWareId();
            String msg = "库存锁定失败，当前：" + skuId + "商品库存不够无法锁定,路径：" + "com.merchen.gulimall.ware.exception.LockStockException";
            if (wareIds == null || wareIds.size() == 0) {
                throw new LockStockException(msg);
            }
            //遍历每个sku，sku对应的每个仓库id
            // 1-2-1 2-1-3 3-1-4(x) 对于全部的订单回滚
            for (Long wareId : wareIds) {
                //如果当前仓库和sku，库存能锁成功，则更新数据，并返回值
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
                    //每锁定一个库存，发送一条消息给mq
                    //如果某一个商品锁库存失败，则整个方法事务回滚，但是工作单也回滚了，如果只是在消息队列中存放工作单的id，就无法获取哪些商品锁定了库存信息
                    StockLockTo stockLockTo = new StockLockTo();
                    stockLockTo.setId(wareOrderTaskEntity.getId());
                    StockDetailTo detailTo = new StockDetailTo();
                    BeanUtils.copyProperties(wareOrderTaskDetailEntity, detailTo);
                    stockLockTo.setDetail(detailTo);
                    try {
                        rabbitTemplate.convertAndSend("stock-event-exchange", "stock.locked", stockLockTo);
                    } catch (Exception e) {
                        //重试机制
                        Integer step = 10;
                        while (step-- > 0) {
                            rabbitTemplate.convertAndSend("stock-event-exchange", "stock.locked", stockLockTo);
                        }
                    }
                    break;
                } else {
                    //继续下一个仓库id
                }
            }
            //事务回滚机制
            //只要有一个仓库id对应的skuid锁定失败，则全部回滚
            if (!lock) {

                throw new LockStockException(msg);
            }
        }
        return true;
    }

    //消息队列监听数据
    @Override
    public void unLockStock(StockLockTo stockLockTo) {

        //解锁
        // stockLockTo为null ,说明库存服务本身出现异常，无需解锁
        // stockLockTo不为null,获取id，
        //              查询工作单状态，如果db工作单无记录，则工作单回滚了，要解锁库存
        //              如果db工作单有记录，
        //                      库存服务正常，order订单状态取消，解锁
        //                      库存服务正常，order订单没取消，不解锁
        if (stockLockTo != null) {
            //消息对列的数据
            StockDetailTo mqDetail = stockLockTo.getDetail();
            //查询数据库中by ID
            Long id = mqDetail.getId();
            WareOrderTaskDetailEntity dbEntity = wareOrderTaskDetailService.getById(id);
            if (dbEntity == null) {
                //锁库存业务回滚，不用解锁
                //mq handle ack
            } else {
                //查询订单
                Long lockToId = stockLockTo.getId();
                WareOrderTaskEntity byId1 = wareOrderTaskService.getById(lockToId);
                R r = orderFeignService.getStatus(byId1.getOrderSn());
                //查询订单的状态
                //库存服务正常，order订单状态取消，解锁
                //库存服务正常，order订单没取消，不解锁
                if (r.getCode() == 0) {
                    OrderTo order = r.getData("data", new TypeReference<OrderTo>() {
                    });
                    //订单服务不存在或者订单状态是取消了
                    if (order == null || order.getStatus() == 4) {
                        //工作单详情是否未解锁
                        if (dbEntity.getLockStatus() == 1) {
                            //解锁
                            unLockWare(mqDetail);
                        }
                    } else {
                        //订单没取消，不解锁,消费数据
                    }
                } else {
                    //处理网络波动，可能远程服务没有访问成功,消息重新放回队列
                    throw new RuntimeException("远程服务失败");
                }
            }

        } else {
            //tockLockTo为null ,说明库存服务本身出现异常，无需解锁
        }

    }

    /**
     * 正常情况：订单未支付，订单释放状态-》库存服务监听队列，库存服务发现订单服务释放-》库存服务解锁库存
     * 解锁订单服务处理网络延迟波动：订单还没有修改状态，库存服务先开始监听发现订单是未结束，不解锁库存了-》订单开始释放-》库存无法解锁
     * 解决:
     * 订单服务发送消息给mq，库存服务的mq监听到OrderEntity，
     * 1>先查询工作单的状态：
     * 1）获去工作单id
     * 2）根据工作单的id获取哪些商品还没有解锁
     * 3）在依次去解锁
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
        //修改工作单详情的状态
        wareOrderTaskDetailService.updateById(wareOrderTaskDetailEntity);
        //手动确认回复

    }

    @Data
    class WareIdHasSkuStock {
        private Long skuId;
        private Integer num;//库存
        private List<Long> wareId;
    }
}