package com.merchen.gulimall.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.merchen.gulimall.product.entity.ProductAttrValueEntity;
import com.merchen.gulimall.product.service.ProductAttrValueService;
import com.merchen.gulimall.product.vo.AttrVO;
import com.merchen.gulimall.product.vo.SpuAttrVO;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.merchen.gulimall.product.service.AttrService;
import com.merchen.common.utils.PageUtils;
import com.merchen.common.utils.R;



/**
 * 商品属性
 *
 * @author ChenZhongBo
 * @email 961208477@qq.com
 * @date 2022-06-04 20:05:18
 */
@Api("商品属性")
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    /**
     * 规格维护
     */
    @GetMapping("/{attrType}/listforspu/{spuId}")
    public R listMaintance(@PathVariable("spuId")Long spuId){
        List<ProductAttrValueEntity>  list = productAttrValueService.getList(spuId);
        return R.ok().put("data", list);
    }

    /**
     * 列表
     */
    @RequestMapping("/{attrType}/list/{catelogId}")
    public R list(@RequestParam Map<String, Object> params,
                  @PathVariable("catelogId")Long catelogId,
                  @PathVariable("attrType")String type){
        PageUtils page = attrService.queryPage(params,catelogId,type);

        return R.ok().put("page", page);
    }


    /**
     * 07、查询属性详情
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    public R info(@PathVariable("attrId") Long attrId){
		AttrVO attrVO = attrService.getDetail(attrId);
        return R.ok().put("attr", attrVO);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrVO attrVO){
		attrService.saveDetail(attrVO);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrVO attrVO){
		attrService.updateDetails(attrVO);

        return R.ok();
    }

    /**
     * 规格维护
     */
    @RequestMapping("/update/{spuId}")
    public R updateSpuAttr(@PathVariable("spuId")Long spuId, @RequestBody List<SpuAttrVO> spuAttrVOS){
        attrService.updateSpuAttr(spuAttrVOS,spuId);

        return R.ok();
    }
    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
