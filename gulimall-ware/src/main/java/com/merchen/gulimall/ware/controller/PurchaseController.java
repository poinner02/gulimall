package com.merchen.gulimall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.merchen.gulimall.ware.vo.MergePurchaseVO;
import com.merchen.gulimall.ware.vo.PurchaseDoneVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.merchen.gulimall.ware.entity.PurchaseEntity;
import com.merchen.gulimall.ware.service.PurchaseService;
import com.merchen.common.utils.PageUtils;
import com.merchen.common.utils.R;



/**
 * 采购信息
 *
 * @author ChenZhongBo
 * @email 961208477@qq.com
 * @date 2022-06-05 19:23:27
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Autowired

    private PurchaseService purchaseService;



    @PostMapping("/done")
    public R done(@RequestBody PurchaseDoneVO purchaseDoneVO){
        purchaseService.done(purchaseDoneVO);
        return R.ok();
    }


    @PostMapping("/received")
    public R receive(@RequestBody List<Long> ids){
        purchaseService.receive(ids);
        return R.ok();
    }

    @RequestMapping("/merge")
    public R merge(@RequestBody MergePurchaseVO mergePurchaseVO){
        purchaseService.merge(mergePurchaseVO);
        return R.ok();
    }

    @RequestMapping("/unreceive/list")
    public R unreceivelist(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryUnreceivelist(params);
        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody PurchaseEntity purchase){
		purchaseService.save(purchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody PurchaseEntity purchase){
		purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
