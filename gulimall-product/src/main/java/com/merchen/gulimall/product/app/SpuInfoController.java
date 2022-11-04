package com.merchen.gulimall.product.app;

import java.util.Arrays;
import java.util.Map;

import com.merchen.gulimall.product.vo.SpuSaveVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.merchen.gulimall.product.entity.SpuInfoEntity;
import com.merchen.gulimall.product.service.SpuInfoService;
import com.merchen.common.utils.PageUtils;
import com.merchen.common.utils.R;



/**
 * spu信息
 *
 * @author ChenZhongBo
 * @email 961208477@qq.com
 * @date 2022-06-04 20:05:18
 */
@RestController
@RequestMapping("product/spuinfo")
public class SpuInfoController {
    @Autowired
    private SpuInfoService spuInfoService;

    /**
     * http://localhost:88/api/product/spuinfo/2/up
     * 商品上架功能
     * @param
     * @return
     */

    @PostMapping("/{spuId}/up")
    public R up(@PathVariable("spuId")Long spuId){
        spuInfoService.upProduct(spuId);
        return R.ok();
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = spuInfoService.queryPageOnCondiction(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		SpuInfoEntity spuInfo = spuInfoService.getById(id);

        return R.ok().put("spuInfo", spuInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody SpuSaveVO saveVO){
        //todo jsr303
//		spuInfoService.save(spuInfo);
        spuInfoService.saveSpuInfo(saveVO);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody SpuInfoEntity spuInfo){
		spuInfoService.updateById(spuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		spuInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
