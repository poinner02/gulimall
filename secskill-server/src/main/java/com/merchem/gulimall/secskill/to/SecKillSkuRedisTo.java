package com.merchem.gulimall.secskill.to;

import com.merchem.gulimall.secskill.vo.SkuInfoVo;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author MrChen
 * @create 2022-10-07 22:08
 */
@Data
public class SecKillSkuRedisTo {
    /**
     * id
     */
    private Long id;
    /**
     * 活动id
     */
    private Long promotionId;
    /**
     * 活动场次id
     */
    private Long promotionSessionId;
    /**
     * 商品id
     */
    private Long skuId;
    /**
     * 秒杀价格
     */
    private BigDecimal seckillPrice;
    /**
     * 秒杀总量
     */
    private BigDecimal seckillCount;
    /**
     * 每人限购数量
     */
    private BigDecimal seckillLimit;
    /**
     * 排序
     */
    private Integer seckillSort;



    private Long startTime;
    private Long endTime;

    private String radomCode;

    private SkuInfoVo skuInfoVo;


}
