package com.merchen.gulimall.coupon.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.merchen.common.utils.PageUtils;
import com.merchen.common.utils.Query;

import com.merchen.gulimall.coupon.dao.SeckillPromotionDao;
import com.merchen.gulimall.coupon.entity.SeckillPromotionEntity;
import com.merchen.gulimall.coupon.service.SeckillPromotionService;
import org.springframework.util.StringUtils;


@Service("seckillPromotionService")
public class SeckillPromotionServiceImpl extends ServiceImpl<SeckillPromotionDao, SeckillPromotionEntity> implements SeckillPromotionService {

    /**
     *
     * @param params t=1664535499976&page=1&limit=10&key=
     * @return
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<SeckillPromotionEntity> wrapper = new QueryWrapper<>();
        //条件查询
        if(!StringUtils.isEmpty(params.get("key"))){
            /**
             * id           bigint(20)    (NULL)              NO      PRI     (NULL)   auto_increment  select,insert,update,references  id
             * title        varchar(255)  utf8mb4_general_ci  YES             (NULL)                   select,insert,update,references  活动标题
             * start_time   datetime      (NULL)              YES             (NULL)                   select,insert,update,references  开始日期
             * end_time     datetime      (NULL)              YES             (NULL)                   select,insert,update,references  结束日期
             * status       tinyint(4)    (NULL)              YES             (NULL)                   select,insert,update,references  上下线状态
             * create_time  datetime      (NULL)              YES             (NULL)                   select,insert,update,references  创建时间
             * user_id      bigint(20)    (NULL)              YES             (NULL)                   select,insert,update,references  创建人
             */
            String key = (String) params.get("key");
            wrapper.and(o->o.like("title", key).or().eq("id", key));
        }

        IPage<SeckillPromotionEntity> page = this.page(
                new Query<SeckillPromotionEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }

}