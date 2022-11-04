package com.merchem.gulimall.secskill.service.Imp;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.merchem.gulimall.secskill.feign.CouponFeignService;
import com.merchem.gulimall.secskill.feign.ProductFeignService;
import com.merchem.gulimall.secskill.interceptor.MyLoginInterceptor;
import com.merchem.gulimall.secskill.service.SecSkillService;
import com.merchem.gulimall.secskill.to.SecKillSkuRedisTo;
import com.merchem.gulimall.secskill.vo.SeckillSessionEntityVo;
import com.merchem.gulimall.secskill.vo.SkuInfoVo;
import com.merchen.common.exception.BizCodeEnume;
import com.merchen.common.to.mq.SeckillOrderTo;
import com.merchen.common.utils.R;
import com.merchen.common.vo.MemberResponVo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author MrChen
 * @create 2022-10-07 21:34
 */
@Slf4j
@Service
public class SecSkillServiceImp implements SecSkillService {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    private CouponFeignService couponFeignService;
    @Autowired
    RedissonClient redissonClient;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ProductFeignService productFeignService;

    private final String SESSION_CACHE_PREFIX = "seckill:session:";
    private final String SKUKILL_CACHE_PREFIX = "seckill:skus";
    private final String SKU_STOCK_SEMAPHORE = "seckill:stock:";


    @Override
    public void getSecSkill3DayCoupon() {


        //获取近3天的秒杀活动
        R r = couponFeignService.getSeckillSessionRecenty3Day();
        if (r.getCode() == 0) {
            List<SeckillSessionEntityVo> list = r.getData("data", new TypeReference<List<SeckillSessionEntityVo>>() {
            });
            if (list != null && list.size() > 0) {
                //缓存所有的活动信息->skuId
                saveSessionIngfos(list);
                //缓存所有的关联商品信息
                saveSessionSkuInfos(list);

            }
        }

    }

    @SentinelResource(blockHandler = "blockHandlerMethod",value = "getCurrentSecKill")
    @Override
    public List<SecKillSkuRedisTo> getCurrentSecKill() {
        long currentTime = new Date().getTime();
        Set<String> keys = redisTemplate.keys(SESSION_CACHE_PREFIX + "*");
        for (String key : keys) {
            //guilishop:0>keys seckill:session:*
            //1)  "seckill:session:1665417600000_1665676800000"
            //2)  "seckill:session:1665504000000_1667059200000"
            String time = key.replace("seckill:session:", "");
            String[] s = time.split("_");
            Long startTime = Long.parseLong(s[0]);
            Long endTime = Long.parseLong(s[1]);
            if (currentTime > startTime && currentTime < endTime) {
                List<String> range = redisTemplate.opsForList().range(key, -100, 100);
                BoundHashOperations<String, String, Object> ops = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
                List<Object> list = ops.multiGet(range);
                if (list != null && list.size() > 0) {
                    List<SecKillSkuRedisTo> collect = list.stream().map(item -> {
                        SecKillSkuRedisTo secKillSkuRedisTo = JSON.parseObject((String) item, new TypeReference<SecKillSkuRedisTo>() {
                        });
                        return secKillSkuRedisTo;
                    }).collect(Collectors.toList());
                    return collect;
                }
                break;
            }
        }
        return null;
    }
    public List<SecKillSkuRedisTo> blockHandlerMethod(BlockException e){
        return null;
    }

    @Override
    public SecKillSkuRedisTo getSecKillSkuInfo(Long skuId) {

        BoundHashOperations<String, String, String> ops = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
        Set<String> keys = ops.keys();
        SecKillSkuRedisTo killSkuRedisTo = new SecKillSkuRedisTo();
        if (keys != null && keys.size() > 0) {
            //正则匹配
            String regx = "\\d-" + skuId;
            for (String key : keys) {
                if (Pattern.matches(regx, key)) {
                    String json = ops.get(key);
                    killSkuRedisTo = JSON.parseObject(json, SecKillSkuRedisTo.class);
                    //获取是否是当前时间应该秒杀的商品
                    long current = new Date().getTime();
                    Long startTime = killSkuRedisTo.getStartTime();
                    Long endTime = killSkuRedisTo.getEndTime();
                    if (current >= startTime && current <= endTime) {
                        //不做任何处理
                    } else {
                        //隐藏随机码
                        killSkuRedisTo.setRadomCode("");
                    }
                    break;
                }
//                String[] split = key.split("-");
//                if(split[1].equals(""+skuId)){
//                    String s = ops.get(key);
//                    killSkuRedisTo = JSON.parseObject(s, SecKillSkuRedisTo.class);
//                    break;
//                }
            }
        }
        return killSkuRedisTo;
    }

    /***
     * 秒杀商品
     * @param killId
     * @param key
     * @param num
     * @return
     */
    @Override
    public String secKill(String killId, String key, Integer num) {

        ThreadLocal<MemberResponVo> loginUser = MyLoginInterceptor.loginUser;

        //http://seckill.gulimall.com/kill?killId=2_1&key=938194f528ad42869d0200935fab53ac&num=1
        //校验数据
        BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
        //前端数据 2_1  redis数据 2-1
        String replace = killId.replace("_", "-");
        Object o = ops.get(replace);
        //获取缓存中的秒杀商品
        SecKillSkuRedisTo redis = JSON.parseObject((String) o, SecKillSkuRedisTo.class);
        Long endTime = redis.getEndTime();
        Long startTime = redis.getStartTime();
        long currentTime = new Date().getTime();
        //校验秒杀商品时间未过期
        if (currentTime >= startTime && currentTime <= endTime) {
            //校验随机码
            String radomCode = redis.getRadomCode();
            if (radomCode.equals(key)) {
                //true 随机码通过
                //校验数量
                BigDecimal seckillLimit = redis.getSeckillLimit();
                if (num <= seckillLimit.intValue()) {
                    MemberResponVo memberResponVo = loginUser.get();
                    String preSent = memberResponVo.getId() + "_" + redis.getPromotionSessionId();
                    long ttl = endTime - startTime;
                    //秒杀占位
                    Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent(preSent, num.toString(), ttl, TimeUnit.MILLISECONDS);
                    if (aBoolean) {
                        //占位成功
                        RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + key);
                        //秒杀抢占资源 信号量
                        boolean b = semaphore.tryAcquire(num);
                        if (b) {
                            //生成订单号
                            String orderId = IdWorker.getTimeId();
                            SeckillOrderTo seckillOrderTo = new SeckillOrderTo();
                            seckillOrderTo.setOrderSn(orderId);
                            seckillOrderTo.setSkuId(redis.getSkuId());
                            seckillOrderTo.setNum(num);
                            seckillOrderTo.setSecKillPrice(redis.getSeckillPrice());
                            seckillOrderTo.setMemberId(memberResponVo.getId());
                            seckillOrderTo.setPromotionSessionId(redis.getPromotionSessionId());
                            //发送秒杀订单给消息队列
                            rabbitTemplate.convertAndSend("order-event-exchange", "order.seckill.order", seckillOrderTo);
                            return orderId;
                        }
                    } else {
                        //已经购买了
                    }
                }
            }
        }
        return null;
    }

    private void saveSessionSkuInfos(List<SeckillSessionEntityVo> list) {
        list.stream().forEach(item -> {
            BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
            item.getRelationEntities().stream().forEach(rel -> {
                String token = UUID.randomUUID().toString().replace("-", "").toString();
                if (!ops.hasKey(rel.getSkuId().toString())) {
                    //秒杀商品信息
                    SecKillSkuRedisTo secKillSkuRedisTo = new SecKillSkuRedisTo();
                    BeanUtils.copyProperties(rel, secKillSkuRedisTo);
                    //秒杀商品基本信息
                    SkuInfoVo skuInfoVo = productFeignService.seckillRemoteInfo(rel.getSkuId());
//                    log.info("=====================================SkuInfoVo:{}",skuInfoVo);
                    secKillSkuRedisTo.setSkuInfoVo(skuInfoVo);
                    secKillSkuRedisTo.setStartTime(item.getStartTime().getTime());
                    secKillSkuRedisTo.setEndTime(item.getEndTime().getTime());
                    //秒杀商品随机码
                    secKillSkuRedisTo.setRadomCode(token);
                    //todo 分布式信号量，限流
                    String jsonString = JSON.toJSONString(secKillSkuRedisTo);
                    ops.put(rel.getPromotionSessionId().toString() + "-" + rel.getSkuId().toString(), jsonString);
                    RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + token);
                    semaphore.trySetPermits(rel.getSeckillCount().intValue());
                }

            });
        });

    }

    //solve bug 新增秒杀商品没有写入redis中
    private void saveSessionIngfos(List<SeckillSessionEntityVo> list) {
        list.forEach(session -> {
            Long startTime = session.getStartTime().getTime();
            Long endTime = session.getEndTime().getTime();
            String key = SESSION_CACHE_PREFIX + startTime + "_" + endTime;
            redisTemplate.delete(key);
            if (session.getRelationEntities() != null || session.getRelationEntities().size() > 0) {
                List<String> collect = session.getRelationEntities().stream().map(item -> {
                    //s = 秒杀id-商品id
                    String s = item.getPromotionSessionId() + "-" + item.getSkuId().toString();
                    return s;
                }).collect(Collectors.toList());
                redisTemplate.opsForList().leftPushAll(key, collect);
            }
            //olde 代码
//            if (!aBoolean) {
//
//                if (session.getRelationEntities() != null || session.getRelationEntities().size() > 0) {
//                    List<String> collect = session.getRelationEntities().stream().map(item -> {
//                        //s = 秒杀id-商品id
//                        String s = item.getPromotionSessionId() + "-" + item.getSkuId().toString();
//                        return s;
//                    }).collect(Collectors.toList());
//                    redisTemplate.opsForList().leftPushAll(key, collect);
//                }
//            }
        });
    }
}
