package com.merchem.gulimall.secskill.service;

import com.merchem.gulimall.secskill.to.SecKillSkuRedisTo;
import com.merchen.common.utils.R;

import java.util.List;

/**
 * @author MrChen
 * @create 2022-10-07 21:33
 */
public interface SecSkillService {

    public void getSecSkill3DayCoupon();

    List<SecKillSkuRedisTo> getCurrentSecKill();

    SecKillSkuRedisTo getSecKillSkuInfo(Long skuId);

    String secKill(String killId, String key, Integer num);
}
