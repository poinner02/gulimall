package com.merchen.gulimall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.merchen.common.exception.LockStockException;
import com.merchen.common.to.SkuHasStockTO;
import com.merchen.gulimall.ware.vo.WareSkuLockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.merchen.gulimall.ware.entity.WareSkuEntity;
import com.merchen.gulimall.ware.service.WareSkuService;
import com.merchen.common.utils.PageUtils;
import com.merchen.common.utils.R;


/**
 * 商品库存
 *
 * @author ChenZhongBo
 * @email 961208477@qq.com
 * @date 2022-06-05 19:23:27
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;

    @PostMapping("/orderLockStock")
    public R orderLockStock(@RequestBody WareSkuLockVo wareSkuLockVo) {
        try {
            Boolean flag = wareSkuService.orderLockStock(wareSkuLockVo);
            return R.ok();
        } catch (LockStockException e) {
            return R.error().put("msg", e.getMessage());
        }
    }

    /**
     * 远程接口调用是否有库存根据id集合
     */
    @PostMapping("/hasStock")
    public R getSkuHasStock(@RequestBody List<Long> skuIds) {
        //skuId -> stockl
        List<SkuHasStockTO> list = wareSkuService.hasStock(skuIds);
        return R.ok().setDate(list);
//        List<SkuHasStockTO> skuHasStockTOS = wareSkuService.hasStock(skuIds);
//        ResultEntity resultEntity = ResultEntity.OK().setData(skuHasStockTOS);
//        return  resultEntity;
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WareSkuEntity wareSku) {
        wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WareSkuEntity wareSku) {
        wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
