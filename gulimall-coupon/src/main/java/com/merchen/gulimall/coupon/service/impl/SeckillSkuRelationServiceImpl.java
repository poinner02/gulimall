package com.merchen.gulimall.coupon.service.impl;

import com.merchen.gulimall.coupon.entity.SeckillSessionEntity;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.merchen.common.utils.PageUtils;
import com.merchen.common.utils.Query;

import com.merchen.gulimall.coupon.dao.SeckillSkuRelationDao;
import com.merchen.gulimall.coupon.entity.SeckillSkuRelationEntity;
import com.merchen.gulimall.coupon.service.SeckillSkuRelationService;
import org.springframework.util.StringUtils;


@Service("seckillSkuRelationService")
public class SeckillSkuRelationServiceImpl extends ServiceImpl<SeckillSkuRelationDao, SeckillSkuRelationEntity> implements SeckillSkuRelationService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<SeckillSkuRelationEntity> wrapper = new QueryWrapper<>();
        //条件查询
        if(!StringUtils.isEmpty(params.get("key"))){
            /**
             * id                    bigint(20)     (NULL)     NO      PRI     (NULL)   auto_increment  select,insert,update,references  id
             * promotion_id          bigint(20)     (NULL)     YES             (NULL)                   select,insert,update,references  活动id
             * promotion_session_id  bigint(20)     (NULL)     YES             (NULL)                   select,insert,update,references  活动场次id
             * sku_id                bigint(20)     (NULL)     YES             (NULL)                   select,insert,update,references  商品id
             * seckill_price         decimal(10,0)  (NULL)     YES             (NULL)                   select,insert,update,references  秒杀价格
             * seckill_count         decimal(10,0)  (NULL)     YES             (NULL)                   select,insert,update,references  秒杀总量
             * seckill_limit         decimal(10,0)  (NULL)     YES             (NULL)                   select,insert,update,references  每人限购数量
             * seckill_sort          int(11)        (NULL)     YES             (NULL)                   select,insert,update,references  排序
             */
            String key = (String) params.get("key");
            wrapper.eq("sku_id",key).or().eq("seckill_count", key);
        }
        wrapper.eq("promotion_session_id", params.get("promotionSessionId"));
        IPage<SeckillSkuRelationEntity> page = this.page(
                new Query<SeckillSkuRelationEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }

}