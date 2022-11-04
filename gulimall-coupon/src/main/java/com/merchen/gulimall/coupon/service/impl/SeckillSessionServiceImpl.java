package com.merchen.gulimall.coupon.service.impl;

import com.merchen.gulimall.coupon.entity.SeckillPromotionEntity;
import com.merchen.gulimall.coupon.entity.SeckillSkuRelationEntity;
import com.merchen.gulimall.coupon.service.SeckillSkuRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.merchen.common.utils.PageUtils;
import com.merchen.common.utils.Query;

import com.merchen.gulimall.coupon.dao.SeckillSessionDao;
import com.merchen.gulimall.coupon.entity.SeckillSessionEntity;
import com.merchen.gulimall.coupon.service.SeckillSessionService;
import org.springframework.util.StringUtils;


@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService {

    @Autowired
    private SeckillSkuRelationService seckillSkuRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        QueryWrapper<SeckillSessionEntity> wrapper = new QueryWrapper<>();
        //条件查询
        if(!StringUtils.isEmpty(params.get("key"))){
            /**
             * id           bigint(20)    (NULL)              NO      PRI     (NULL)   auto_increment  select,insert,update,references  id
             * name         varchar(200)  utf8mb4_general_ci  YES             (NULL)                   select,insert,update,references  场次名称
             * start_time   datetime      (NULL)              YES             (NULL)                   select,insert,update,references  每日开始时间
             * end_time     datetime      (NULL)              YES             (NULL)                   select,insert,update,references  每日结束时间
             * status       tinyint(1)    (NULL)              YES             (NULL)                   select,insert,update,references  启用状态
             * create_time  datetime      (NULL)              YES             (NULL)                   select,insert,update,references  创建时间
             */
            String key = (String) params.get("key");
            wrapper.and(o->o.like("name",key)).or().eq("id", key);
        }
        IPage<SeckillSessionEntity> page = this.page(
                new Query<SeckillSessionEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }

    /**
     * 获取近3天的秒杀列表
     * @return
     */
    @Override
    public List<SeckillSessionEntity> getSeckillSession() {
        //2022-10-1 00:00:00 ~ 2022-10-3 23:59:59
        List<SeckillSessionEntity> list = this.list(new QueryWrapper<SeckillSessionEntity>().between("start_time", startIime(), endTime()));
        if(list!=null && list.size()>0){
            List<SeckillSessionEntity> collect = list.stream().map(seckill -> {
                Long id = seckill.getId();
                List<SeckillSkuRelationEntity> relationEntities = seckillSkuRelationService.list(new QueryWrapper<SeckillSkuRelationEntity>().eq("promotion_session_id", id));
                seckill.setRelationEntities(relationEntities);
                return seckill;
            }).collect(Collectors.toList());
        }
        return list;
    }

    /**
     * 获取开始时间
     * @return
     */
    private String startIime() {
        //格式 yyyy-MM-dd HH:mm:ss 例子 2022-10-1 00:00:00
        LocalDateTime of = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        String format = of.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return format;
    }

    /**
     * 获取结束时间
     * @return
     */
    private String endTime() {
        LocalDateTime of = LocalDateTime.of(LocalDate.now().plusDays(3), LocalTime.MAX);
        String format = of.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return format;
    }


}