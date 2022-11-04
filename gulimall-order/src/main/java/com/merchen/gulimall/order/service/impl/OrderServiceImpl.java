package com.merchen.gulimall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.merchen.common.constant.AuthServiceConstant;
import com.merchen.common.constant.OrderConstant;
import com.merchen.common.constant.OrderStatusEnum;
import com.merchen.common.exception.LockStockException;
import com.merchen.common.to.SkuHasStockTO;
import com.merchen.common.to.mq.OrderEntityTo;
import com.merchen.common.to.mq.SeckillOrderTo;
import com.merchen.common.utils.R;
import com.merchen.common.vo.CartItem;
import com.merchen.common.vo.MemberResponVo;
import com.merchen.gulimall.order.entity.OrderItemEntity;
import com.merchen.gulimall.order.entity.PaymentInfoEntity;
import com.merchen.gulimall.order.feign.CartFeignService;
import com.merchen.gulimall.order.feign.MemberFeignService;
import com.merchen.gulimall.order.feign.ProductFeignService;
import com.merchen.gulimall.order.feign.WareFeignService;
import com.merchen.gulimall.order.interceptor.MyLoginInterceptor;
import com.merchen.gulimall.order.service.OrderItemService;
import com.merchen.gulimall.order.service.PaymentInfoService;
import com.merchen.gulimall.order.to.OrderCreateTo;
import com.merchen.gulimall.order.vo.*;
//import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.hash.BeanUtilsHashMapper;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.merchen.common.utils.PageUtils;
import com.merchen.common.utils.Query;

import com.merchen.gulimall.order.dao.OrderDao;
import com.merchen.gulimall.order.entity.OrderEntity;
import com.merchen.gulimall.order.service.OrderService;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {


    private ThreadLocal<OrderSubmitVo> submitVoThreadLocal = new ThreadLocal<>();
    @Value("{alipay.app_id}")
    private String appid;

    @Value("alipay.alipay_public_key")
    private String alipay_public_key;

    @Autowired
    private PaymentInfoService paymentInfoService;


    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    MemberFeignService memberFeignService;

    @Autowired
    OrderService orderService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    CartFeignService cartFeignService;

    @Autowired
    ThreadPoolExecutor executor;

    @Autowired
    WareFeignService wareFeignService;


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 类本类调用本地事务，注意
     *
     * <dependency>
     * <groupId>org.springframework.boot</groupId>
     * <artifactId>spring-boot-starter-aop</artifactId>
     * </dependency>
     *
     * @EnableAspectJAutoProxy(exposeProxy = true)
     * 使用AopContext.currentProxy()代理业务方法，处理类本类的调用事务失效问题,或者注入ioc别的service的事务方法
     * OrderServiceImpl orderService = (OrderServiceImpl) AopContext.currentProxy();
     */
    @Transactional
    public void a() {
        OrderServiceImpl orderService = (OrderServiceImpl) AopContext.currentProxy();
        orderService.b();
        orderService.c();
    }

    /**
     * 1）READ_UNCOMMITTED 读未提交数据，脏读，当其他事务回滚后,当前事务读取未提交的数据，即成为脏读
     * 2) READ_COMMITTED   不可重复读
     * 3) REPEATABLE_READ 可重复读
     * 4) SERIALIZABLE
     * propagation = Propagation.REQUIRED： 没有事务，自己创建一个，如果有事务，用它的事务
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.REQUIRED)
    public void b() {

    }

    /**
     * propagation = Propagation.REQUIRES_NEW :没有事务，自己创建一个，如果有事务，当前自己还是创建一个事务
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void c() {

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 后端接口查询所有的订单
     *
     * @param params
     * @return
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<OrderEntity> wrapper = new QueryWrapper<>();
        //条件查询
        if (!StringUtils.isEmpty(params.get("key"))) {
            String key = (String) params.get("key");
            wrapper.eq("member_id", key)
                    .or().eq("order_sn", key)
                    .or().eq("coupon_id", key)
                    .or().like("member_username", key)
                    .or().eq("total_amount", key)
                    .or().eq("pay_type", key)
                    .or().eq("status", key)
                    .or().eq("growth", key)
                    .or().like("note", key);
        }
        //分页查询
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }

    /**
     * 异步编排和解决openfeign丢失请求信息的问题
     *
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Override
    public OrderVo getOrderVo() throws ExecutionException, InterruptedException {

        OrderVo orderVo = new OrderVo();
        //从线程中获取MemberResponVo
        MemberResponVo memberResponVo = MyLoginInterceptor.loginUser.get();
        //获取request上下文
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        //异步编排
        CompletableFuture<Void> adressFuture = CompletableFuture.runAsync(() -> {
            /**
             *  ①获取地址
             *  调用远程微服务
             *  try catch 处理网络波动
             */
            try {
                //共享主线程的数据给每一个线程
                RequestContextHolder.setRequestAttributes(requestAttributes);
                //远程调用
                R r = memberFeignService.list(memberResponVo.getId());
                List<MemberReceiveAddressEntity> list = r.getData("data", new TypeReference<List<MemberReceiveAddressEntity>>() {
                });
                orderVo.setAdresses(list);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, executor);
        CompletableFuture<Void> CartListFuture = CompletableFuture.runAsync(() -> {
            //共享主线程的数据给每一个线程
            RequestContextHolder.setRequestAttributes(requestAttributes);
            /**
             * 获取购物车详情
             * gulimall:cart:1
             */
            try {
                List<CartItem> cartItems = cartFeignService.getCartItems();
                orderVo.setCartItemList(cartItems);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, executor).thenRunAsync(() -> {
            //获取skuids
            List<Long> collect = orderVo.getCartItemList().stream().map(item -> {
                return item.getSkuId();
            }).collect(Collectors.toList());
            try {
                //远程接口获取SkuHasStockTO
                R r = wareFeignService.getSkuHasStock(collect);
                List<SkuHasStockTO> skuHasStockTOS = r.getData("data", new TypeReference<List<SkuHasStockTO>>() {
                });
                Map<Long, Boolean> map = skuHasStockTOS.stream().collect(Collectors.toMap(SkuHasStockTO::getSkuId, v -> {
                    return v.getStock() > 0;
                }));
                //是否有库存
                orderVo.setHastcokMap(map);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, executor);

        CompletableFuture.allOf(adressFuture, CartListFuture).get();
        /**
         * 获取优惠信息
         */
        //todo 获取优惠信息
        orderVo.setReduce(new BigDecimal("" + 0));
        //生成防重令牌
        String token = UUID.randomUUID().toString().replace("-", "");
        //令牌放入到redis中，30分过期
        redisTemplate.opsForValue().set(OrderConstant.ORDER_TOKEN_PREFIX + memberResponVo.getId(), token, 30, TimeUnit.MINUTES);
        //为页面渲染储存一个令牌,用来和redis的做校验
        orderVo.setToken(token);
        return orderVo;
    }

    @Override
    public void saveAdress(MemberReceiveAddressEntity memberReceiveAddressEntity) {
        try {
            //远程调用接口
            R r = memberFeignService.save(memberReceiveAddressEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 每个微服务数据库创建undo_log表
     * 安装事务协调器https://github.com/seata/seata/releases
     * 导入依赖<dependency>
     * <groupId>com.alibaba.cloud</groupId>
     * <artifactId>spring-cloud-starter-alibaba-seata</artifactId>
     * <exclusions>
     * <exclusion>
     * <groupId>io.seata</groupId>
     * <artifactId>seata-all</artifactId>
     * </exclusion>
     * </exclusions>
     * </dependency>
     * <dependency>
     * <groupId>io.seata</groupId>
     * <artifactId>seata-all</artifactId>
     * <version>0.9.0</version>
     * </dependency>
     * seata注册到注册中心
     * 使用注解@GlobalTransactional
     * 用到分布式的微服务，使用seata DataSourceProxy代理数据源
     * 分布式事务包含以下这些rpc服务{saveOrderAndOrderItems，orderLockStock}
     *
     * @param submitVo
     * @return
     */
    //todo 保存订单到数据库带事务
    @Transactional
//    @GlobalTransactional
    @Override
    public OrderSubmitResponseVo submitOrder(OrderSubmitVo submitVo) {
        //从线程中获取MemberResponVo
        MemberResponVo memberResponVo = MyLoginInterceptor.loginUser.get();
        //线程中设置OrderSubmitVo
        submitVoThreadLocal.set(submitVo);
        OrderSubmitResponseVo orderSubmitResponseVo = new OrderSubmitResponseVo();
        //前面页面传过来的OrderSubmitVo
        //获取令牌，幂等
        String token = submitVo.getToken();
        if (!StringUtils.isEmpty(token)) {
            //有令牌 lua脚本原子操作，1 删除成功  0删除失败 重复提交订单的幂等性
            String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
            Long del = redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList(OrderConstant.ORDER_TOKEN_PREFIX + memberResponVo.getId()), token);
            if (del != 0) {
                //原子删除成功，返回状态码0
                //执行下单逻辑
                OrderCreateTo order = createOrder();
                //验价
                BigDecimal voPayePrice = submitVo.getPayePrice();
                BigDecimal orderPrice = order.getOrder().getPayAmount();
                if (Math.abs(voPayePrice.subtract(orderPrice).doubleValue()) < 0.01) {
                    //保存db
                    orderService.saveOrderAndOrderItems(order);
                    //锁库存
                    WareSkuLockVo wareSkuLockVo = new WareSkuLockVo();
                    wareSkuLockVo.setOrderSn(order.getOrder().getOrderSn());//设置锁库存的订单号
                    //获取锁库存的订单list
                    List<OrderItemEntity> orderItemEntities = order.getOrderItemList().stream().map(item -> {
                        OrderItemEntity entity = new OrderItemEntity();
                        entity.setSkuId(item.getSkuId());
                        entity.setSkuQuantity(item.getSkuQuantity());
                        entity.setSkuName(item.getSkuName());
                        return entity;
                    }).collect(Collectors.toList());
                    wareSkuLockVo.setLocks(orderItemEntities);
                    R r = wareFeignService.orderLockStock(wareSkuLockVo);
                    if (r.getCode() == 0) {
                        //锁定库存成功
                        orderSubmitResponseVo.setCode(0);
                        orderSubmitResponseVo.setOrderEntity(order.getOrder());
                        //① 发送消息给订单延时队列
                        try {
                            rabbitTemplate.convertAndSend("order-event-exchange", "order.create.order", order.getOrder());
                        } catch (Exception e) {
                            //while重试发送
                            Integer count = 10;
                            while (count-- > 0) {
                                rabbitTemplate.convertAndSend("order-event-exchange", "order.create.order", order.getOrder());
                            }
                        }
                    } else {
                        //没有锁成功
                        orderSubmitResponseVo.setCode(3);
                        String msg = r.getData("msg", new TypeReference<String>() {
                        });
//                        System.out.println(msg);
                        throw new LockStockException(msg);
                    }
                } else {
                    orderSubmitResponseVo.setCode(2);
                }
            } else {
                //token删除失败,返回状态码1
                orderSubmitResponseVo.setCode(1);
            }

        } else {
            //没有令牌返回错误
            orderSubmitResponseVo.setCode(500);
            orderSubmitResponseVo.setOrderEntity(null);
        }

        //无论如何都返回orderSubmitResponseVo
        return orderSubmitResponseVo;

    }


    @Override
    public void saveOrderAndOrderItems(OrderCreateTo order) {
        this.save(order.getOrder());
        orderItemService.saveBatch(order.getOrderItemList());
    }

    /**
     * 关闭队列中订单
     * 延迟队列处理订单未支付
     *
     * @param order
     */
    @Override
    public void releaseOrder(OrderEntity order) {
        //
        OrderEntity orderEntity = this.getById(order.getId());
        //订单状态未付款
        if (orderEntity.getStatus() == OrderStatusEnum.CREATE_NEW.getCode()) {
            OrderEntity updateOrderEntity = new OrderEntity();
            updateOrderEntity.setId(order.getId());
            updateOrderEntity.setStatus(OrderStatusEnum.CANCLED.getCode());
            this.updateById(updateOrderEntity);
            OrderEntityTo orderEntityTo = new OrderEntityTo();
            BeanUtils.copyProperties(order, orderEntityTo);
            try {
                //② 发送消息给库存的消息队列
                rabbitTemplate.convertAndSend("order-event-exchange", "order.release.other", orderEntityTo);
            } catch (Exception e) {
                //重试机制
                Integer count = 10;
                while (count-- > 0) {
                    rabbitTemplate.convertAndSend("order-event-exchange", "order.release.other", orderEntityTo);
                }
            }
        }
    }

    //根据订单号查询
    @Override
    public PayVo payOrder(String orderSn) {
        OrderEntity orderEntity = this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
        List<OrderItemEntity> order_sn = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", orderSn));
        String skuName = order_sn.get(0).getSkuName();
        String skuAttrsVals = order_sn.get(0).getSkuAttrsVals();
        PayVo payVo = new PayVo();
        payVo.setOut_trade_no(orderSn);
        payVo.setTotal_amount(orderEntity.getTotalAmount().setScale(2, BigDecimal.ROUND_UP));
        payVo.setBody(skuAttrsVals);
        payVo.setSubject(skuName);
        return payVo;
    }

    @Override
    public PageUtils listWitchOrderItem(Map<String, Object> params) {
        //登录状态获取threadlocal
        MemberResponVo memberResponVo = MyLoginInterceptor.loginUser.get();
        Long id = memberResponVo.getId();
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>().eq("member_id", id).orderByDesc("id")
        );
        List<OrderEntity> order_sn1 = page.getRecords().stream().map(order -> {
            String orderSn = order.getOrderSn();
            List<OrderItemEntity> order_sn = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", orderSn));
            order.setItemEntities(order_sn);
            return order;
        }).collect(Collectors.toList());
        page.setRecords(order_sn1);
        return new PageUtils(page);

    }

    /**
     * 支付宝异步通知支付成功
     * 1. 商家需要验证该通知数据中的 out_trade_no 是否为商家系统中创建的订单号。
     * 2. 判断 total_amount 是否确实为该订单的实际金额（即商家订单创建时的金额）。
     * 3. 校验通知中的 seller_id（或者 seller_email) 是否为 out_trade_no 这笔单据的对应的操作方（有的时候，一个商家可能有多个 seller_id/seller_email）。
     * 4. 验证 app_id 是否为该商家本身。
     * 上述 1、2、3、4 有任何一个验证不通过，则表明本次通知是异常通知，务必忽略。在上述验证通过后商家必须根据支付宝不同类型的业务通知，正确的进行不同的业务处理，并且过滤重复的通知结果数据。在支付宝的业务通知中，只有交易通知状态为 TRADE_SUCCESS 或 TRADE_FINISHED 时，支付宝才会认定为买家付款成功
     *
     * @param payAsyncVo
     */
    @Override
    public String handleAlipayed(PayAsyncVo payAsyncVo) throws AlipayApiException, IllegalAccessException {
        //异步通知验签
        Map<String, String> paramsMap = new HashMap<>(); //将异步通知中收到的所有参数都存放到map中
        //反射
        Field[] declaredFields = payAsyncVo.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            paramsMap.put(field.getName(), field.get(payAsyncVo).toString());
        }
        boolean signVerified = AlipaySignature.rsaCheckV1(paramsMap, alipay_public_key, "utf-8", payAsyncVo.getSign_type()); //调用SDK验证签名
        if (signVerified) {
            String result = getString(payAsyncVo);
            return result;
        } else {
            // TODO 验签失败则记录异常日志，并在response中返回failure.
            return "error";
        }
    }

    /**
     * 保存秒杀订单的信息到db
     * @param seckillOrderTo
     */
    @Transactional
    @Override
    public void createSecKillOrder(SeckillOrderTo seckillOrderTo) {

        OrderEntity orderEntity = new OrderEntity();
        R info = memberFeignService.info(seckillOrderTo.getMemberId());
        if(info.getCode() == 0){
            MemberResponVo memberResponVo = info.getData("member",new TypeReference<MemberResponVo>(){});
            orderEntity.setMemberUsername(memberResponVo.getUsername());
        }
        //保存订单信息
//        orderEntity.setCouponId();
        Date date = new Date();
        orderEntity.setModifyTime(date);
        orderEntity.setCreateTime(date);
        orderEntity.setOrderSn(seckillOrderTo.getOrderSn());
        orderEntity.setMemberId(seckillOrderTo.getMemberId());
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        BigDecimal multiply = seckillOrderTo.getSecKillPrice().multiply(new BigDecimal("" + seckillOrderTo.getNum()));
        orderEntity.setTotalAmount(multiply);
        orderEntity.setPayType(0);
        orderEntity.setStatus(0);
        orderEntity.setGrowth(multiply.intValue());
        orderEntity.setPayAmount(multiply);
        R r = memberFeignService.list(seckillOrderTo.getMemberId());
        if (r.getCode() == 0) {
            List<MemberReceiveAddressEntity> list = r.getData("data", new TypeReference<List<MemberReceiveAddressEntity>>() {
            });
            if (list != null && list.size() > 0) {
                MemberReceiveAddressEntity addressEntity = list.get(0);
                orderEntity.setReceiverCity(addressEntity.getCity() == null ? "" : addressEntity.getCity());
                orderEntity.setReceiverRegion(addressEntity.getRegion() == null ? "" : addressEntity.getRegion());
                orderEntity.setReceiverProvince(addressEntity.getProvince() == null ? "" : addressEntity.getProvince());
                orderEntity.setReceiverPostCode(addressEntity.getPostCode() == null ? "" : addressEntity.getPostCode());
                orderEntity.setReceiverPhone(addressEntity.getPhone() == null ? "" : addressEntity.getPhone());
                orderEntity.setReceiverName(addressEntity.getName() == null ? "" : addressEntity.getName());
                orderEntity.setReceiverDetailAddress(addressEntity.getDetailAddress() == null ? "" : addressEntity.getDetailAddress());
            }
        }
        this.save(orderEntity);
        //todo 保存订单项
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        orderItemEntity.setOrderSn(seckillOrderTo.getOrderSn());
        orderItemEntity.setRealAmount(multiply);
        orderItemEntity.setSkuQuantity(seckillOrderTo.getNum());
        orderItemEntity.setSkuId(seckillOrderTo.getSkuId());
        R r1 = productFeignService.skuInfo(seckillOrderTo.getSkuId());
        if (r1.getCode() == 0) {
            SkuInfoEntity skuInfo = r1.getData("skuInfo", new TypeReference<SkuInfoEntity>() {
            });
            orderItemEntity.setSkuName(skuInfo.getSkuName());
            R r2 = productFeignService.spuInfo(skuInfo.getSpuId());
            if (r2.getCode() == 0) {
                SpuInfoEntity spuInfo = r2.getData("spuInfo", new TypeReference<SpuInfoEntity>() {
                });
//                orderItemEntity.setSpuBrand();
//                orderItemEntity.setSkuAttrsVals();
                orderItemEntity.setSpuId(spuInfo.getId());
                orderItemEntity.setSpuName(spuInfo.getSpuName());
                orderItemEntity.setSpuPic(skuInfo.getSkuDefaultImg());
            }

            orderItemEntity.setSkuPrice(skuInfo.getPrice());
            orderItemEntity.setCategoryId(skuInfo.getCatalogId());
            orderItemEntity.setSkuPic(skuInfo.getSkuDefaultImg());

        }
        orderItemService.save(orderItemEntity);

    }

    private String getString(PayAsyncVo payAsyncVo) {
        //校验通知数据的正确性
        //1. 商家需要验证该通知数据中的 out_trade_no 是否为商家系统中创建的订单号。
        Boolean flag = true;
        String out_trade_no = payAsyncVo.getOut_trade_no();
        OrderEntity order_sn = this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", out_trade_no));
        if (order_sn == null) {
            flag = false;
        }
        //2. 判断 total_amount 是否确实为该订单的实际金额（即商家订单创建时的金额）。
        if (Math.abs(order_sn.getPayAmount().subtract(new BigDecimal(payAsyncVo.getTotal_amount())).doubleValue()) > 0.01) {
            flag = false;
        }
        //todo 3. 校验通知中的 seller_id（或者 seller_email) 是否为 out_trade_no 这笔单据的对应的操作方（有的时候，一个商家可能有多个 seller_id/seller_email）。
        // 4. 验证 app_id 是否为该商家本身。
        if (!appid.equals(payAsyncVo.getApp_id())) {
            flag = false;
        }

        if (flag) {
            try {
                getString(payAsyncVo, order_sn);
            } catch (Exception e) {
                //重试
                int count = 10;
                while (count-- > 0) {
                    getString(payAsyncVo, order_sn);
                }
                throw new RuntimeException("已经支付成功，但是网路问题无法刷新页面");
            }

        }
        return "error";
    }

    @Transactional
    String getString(PayAsyncVo payAsyncVo, OrderEntity order_sn) {
        //保存支付信息到db中
        PaymentInfoEntity paymentInfoEntity = new PaymentInfoEntity();
        paymentInfoEntity.setOrderSn(payAsyncVo.getOut_trade_no());
        paymentInfoEntity.setCreateTime(new Date());
        paymentInfoEntity.setSubject(payAsyncVo.getSubject());
        paymentInfoEntity.setPaymentStatus(payAsyncVo.getTrade_status());
        paymentInfoEntity.setAlipayTradeNo(payAsyncVo.getTrade_no());
//                paymentInfoEntity.setCallbackContent();
        paymentInfoEntity.setCallbackTime(new Date());
        paymentInfoEntity.setConfirmTime(new Date());
        paymentInfoEntity.setTotalAmount(new BigDecimal(payAsyncVo.getTotal_amount()));
        paymentInfoService.save(paymentInfoEntity);

        //在支付宝的业务通知中，只有交易通知状态为 TRADE_SUCCESS 或 TRADE_FINISHED 时，支付宝才会认定为买家付款成功,修改order订单的状态，联调消息队列最终一至性
        String trade_status = payAsyncVo.getTrade_status();
        if ("TRADE_SUCCESS".equals(trade_status) || "TRADE_FINISHED".equals(trade_status)) {
            OrderEntity orderEntity = new OrderEntity();
            orderEntity.setId(order_sn.getId());
            orderEntity.setStatus(1);
            this.updateById(orderEntity);
            return "success";
        }
        return "error";
    }


    private OrderCreateTo createOrder() {
        OrderCreateTo orderCreateTo = new OrderCreateTo();
        //生成订单号
        String orderSn = IdWorker.getTimeId();
        //生成订单总信息
        OrderEntity orderEntity = buildOrderEntity(orderSn);
        //生成订单详情
        List<OrderItemEntity> orderItemEntityList = bulidOrderItemList(orderSn);
        orderCreateTo.setOrderItemList(orderItemEntityList);
        //计算价格
        computePrice(orderEntity, orderItemEntityList);
        orderCreateTo.setOrder(orderEntity);
        return orderCreateTo;
    }

    /**
     * 计算总订单对象中的优惠促销价格,成长值、积分等
     *
     * @param orderEntity
     * @param orderItemEntityList
     */
    private void computePrice(OrderEntity orderEntity, List<OrderItemEntity> orderItemEntityList) {
        //1.order
        BigDecimal total = new BigDecimal("0.0");
        BigDecimal promotion = new BigDecimal("0.0");
        BigDecimal integration = new BigDecimal("0.0");
        BigDecimal coupon = new BigDecimal("0.0");
        Integer gift = new Integer("0");
        Integer growth = new Integer("0");
        for (OrderItemEntity entity : orderItemEntityList) {
            Integer giftIntegration = entity.getGiftIntegration();
            gift += giftIntegration;
            Integer giftGrowth = entity.getGiftGrowth();
            growth += giftGrowth;
            BigDecimal realAmount = entity.getRealAmount();
            BigDecimal promotionAmount = entity.getPromotionAmount();
            promotion = promotion.add(promotionAmount);
            BigDecimal integrationAmount = entity.getIntegrationAmount();
            integration = integration.add(integrationAmount);
            BigDecimal couponAmount = entity.getCouponAmount();
            coupon = coupon.add(couponAmount);
            total = total.add(realAmount);
        }
        //订单总价格不含运费
        orderEntity.setTotalAmount(total);
        //订单应付价格
        orderEntity.setPayAmount(orderEntity.getTotalAmount().add(orderEntity.getFreightAmount()));
        //促销优化金额
        orderEntity.setPromotionAmount(promotion);
        //优惠券抵扣金额
        orderEntity.setCouponAmount(coupon);
        //积分抵扣金额
        orderEntity.setIntegrationAmount(integration);
        //可以获得的积分
        orderEntity.setIntegration(gift);
        //可以获得的成长值
        orderEntity.setGrowth(growth);
        //删除状态【0->未删除；1->已删除】
        orderEntity.setDeleteStatus(0);
    }

    /**
     * 构建订单详情list
     *
     * @param orderSn
     * @return
     */
    private List<OrderItemEntity> bulidOrderItemList(String orderSn) {
        /**
         * feign调用获取购物车详细
         */
        List<CartItem> cartItems = cartFeignService.getCartItems();
        /**
         * 设置订单详情list
         */
        if (cartItems != null && cartItems.size() > 0) {
            List<OrderItemEntity> collect = cartItems.stream().map(cartItem -> {
                OrderItemEntity orderItemEntity = buildOrderItemEntity(orderSn, cartItem);
                return orderItemEntity;
            }).collect(Collectors.toList());
            return collect;
        }
        return null;
    }

    /**
     * 构建订单总信息
     *
     * @param orderSn
     * @return
     */
    private OrderEntity buildOrderEntity(String orderSn) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(orderSn);
        /**
         * 线程获取
         */
        MemberResponVo memberResponVo = MyLoginInterceptor.loginUser.get(); //线程获取登录用户
        orderEntity.setMemberId(memberResponVo.getId());//设置用户id
        orderEntity.setMemberUsername(memberResponVo.getUsername()); //用户名
        /**
         * 线程获取
         */
        OrderSubmitVo orderSubmitVo = submitVoThreadLocal.get();//线程获取cart订单提交后的信息
        /**
         * feign调用
         */
        FareVo fare = wareFeignService.getFare(orderSubmitVo.getAddrId());//远程服务获取地址和运费相关信息
        MemberReceiveAddressEntity memberReceiveAddressEntity = fare.getMemberReceiveAddressEntity(); //获取详细地址
        orderEntity.setFreightAmount(fare.getFare());//设置运费
        /**
         *
         */
        orderEntity.setCreateTime(new Date());//create_time
        orderEntity.setModifyTime(new Date());
        /**
         * 设置收获人信息
         */
        orderEntity.setReceiverCity(memberReceiveAddressEntity.getCity());
        orderEntity.setReceiverDetailAddress(memberReceiveAddressEntity.getDetailAddress());//设置详细地址
        orderEntity.setReceiverName(memberReceiveAddressEntity.getName());
        orderEntity.setReceiverPhone(memberReceiveAddressEntity.getPhone());
        orderEntity.setReceiverPostCode(memberReceiveAddressEntity.getPostCode());
        orderEntity.setReceiverProvince(memberReceiveAddressEntity.getProvince());
        orderEntity.setReceiverRegion(memberReceiveAddressEntity.getRegion());
        /**
         * 设置订单状态信息
         * 订单状态【0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单】
         */
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        orderEntity.setAutoConfirmDay(7);
        return orderEntity;
    }

    /**
     * 构建订单所有项
     *
     * @param orderSn
     * @param cartItem
     * @return
     */
    private OrderItemEntity buildOrderItemEntity(String orderSn, CartItem cartItem) {
        OrderItemEntity orderItemEntity = null;
        try {
            /**
             * feign调用
             */
            R skuR = productFeignService.skuInfo(cartItem.getSkuId());
            SkuInfoEntity skuInfo = skuR.getData("skuInfo", new TypeReference<SkuInfoEntity>() {
            });
            R spuR = productFeignService.spuInfo(skuInfo.getSpuId());
            SpuInfoEntity spuInfo = spuR.getData("spuInfo", new TypeReference<SpuInfoEntity>() {
            });
            R brandR = productFeignService.info(skuInfo.getBrandId());
            BrandEntity brand = brandR.getData("brand", new TypeReference<BrandEntity>() {
            });
            /**
             * 属性对拷
             */
            orderItemEntity = new OrderItemEntity();
            orderItemEntity.setOrderSn(orderSn);
            //1 sku信息
            orderItemEntity.setSkuId(cartItem.getSkuId());
            orderItemEntity.setSkuName(cartItem.getTitel());
            orderItemEntity.setSkuPic(cartItem.getImg());
            orderItemEntity.setSkuAttrsVals(StringUtils.collectionToDelimitedString(cartItem.getAttrs(), ";"));
            orderItemEntity.setSkuQuantity(cartItem.getCount());
            orderItemEntity.setSkuPrice(cartItem.getPrice());
            // 2.积分
            orderItemEntity.setGiftGrowth(cartItem.getPrice().multiply(new BigDecimal(orderItemEntity.getSkuQuantity().toString())).intValue());
            orderItemEntity.setGiftIntegration(cartItem.getPrice().multiply(new BigDecimal(orderItemEntity.getSkuQuantity().toString())).intValue());
            orderItemEntity.setCategoryId(skuInfo.getCatalogId());
            //3 spu信息
            orderItemEntity.setSpuId(spuInfo.getId());
            orderItemEntity.setSpuName(spuInfo.getSpuName());
            orderItemEntity.setSpuBrand(brand.getName());

            //todo 优惠系统未做
            orderItemEntity.setPromotionAmount(new BigDecimal("0.0"));
            orderItemEntity.setCouponAmount(new BigDecimal("0.0"));
            orderItemEntity.setIntegrationAmount(new BigDecimal("0.0"));
            //原始价格
            BigDecimal origin = orderItemEntity.getSkuPrice().multiply(new BigDecimal(orderItemEntity.getSkuQuantity()));
            //优惠后价
            BigDecimal subtract = origin.subtract(orderItemEntity.getCouponAmount())
                    .subtract(orderItemEntity.getPromotionAmount())
                    .subtract(orderItemEntity.getIntegrationAmount());
            orderItemEntity.setRealAmount(subtract);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return orderItemEntity;
    }

}