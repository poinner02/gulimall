package com.merchen.gulimall.ware.dao;

import com.merchen.common.to.SkuHasStockTO;
import com.merchen.gulimall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.merchen.gulimall.ware.vo.WareSkuLockVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品库存
 * 
 * @author ChenZhongBo
 * @email 961208477@qq.com
 * @date 2022-06-05 19:23:27
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    void addStock(@Param("skuId") Long skuId,@Param("wareId") Long wareId,@Param("skuNum") Integer skuNum);

    List<SkuHasStockTO> hasStock(@Param("skuIds") List<Long> skuIds);

    List<Long> wareIdHasSkuStock(@Param("skuId") Long skuId);

    Integer lockSkuStock(@Param("skuId") Long skuId,@Param("wareId") Long wareId,@Param("num") Integer num);

    void modifyWare(@Param("skuId") Long skuId, @Param("wareId") Long wareId,@Param("skuNum") Integer skuNum);
}
