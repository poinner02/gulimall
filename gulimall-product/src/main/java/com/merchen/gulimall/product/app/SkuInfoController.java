package com.merchen.gulimall.product.app;

import java.util.Arrays;
import java.util.Map;

import com.merchen.common.to.ProductSkuInfoTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.merchen.gulimall.product.entity.SkuInfoEntity;
import com.merchen.gulimall.product.service.SkuInfoService;
import com.merchen.common.utils.PageUtils;
import com.merchen.common.utils.R;



/**
 * sku信息
 *
 * @author ChenZhongBo
 * @email 961208477@qq.com
 * @date 2022-06-04 20:05:18
 */
@RestController
@RequestMapping("product/skuinfo")
public class SkuInfoController {
    @Autowired
    private SkuInfoService skuInfoService;


    /**
     * 远程接口
     */
    @RequestMapping("/seckill/remote/info/{skuId}")
    public SkuInfoEntity seckillRemoteInfo(@PathVariable("skuId") Long skuId){
        SkuInfoEntity skuInfo = skuInfoService.getById(skuId);
        return skuInfo;
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = skuInfoService.queryPageOnCondiction(params);

        return R.ok().put("page", page);
    }

    /**
     * 远程接口
     */
    @RequestMapping("/remote/info/{skuId}")
    public R RemoteInfo(@PathVariable("skuId") Long skuId){
        SkuInfoEntity skuInfo = skuInfoService.getById(skuId);
        ProductSkuInfoTO productSkuInfoTO = new ProductSkuInfoTO();
        BeanUtils.copyProperties(skuInfo,productSkuInfoTO);
        return R.ok().put("skuInfoTO", productSkuInfoTO);
    }
    /**
     * 信息
     */
    @RequestMapping("/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId){
		SkuInfoEntity skuInfo = skuInfoService.getById(skuId);

        return R.ok().put("skuInfo", skuInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody SkuInfoEntity skuInfo){
		skuInfoService.save(skuInfo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody SkuInfoEntity skuInfo){
		skuInfoService.updateById(skuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] skuIds){
		skuInfoService.removeByIds(Arrays.asList(skuIds));

        return R.ok();
    }

}
