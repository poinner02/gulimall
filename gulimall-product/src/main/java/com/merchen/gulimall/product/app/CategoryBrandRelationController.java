package com.merchen.gulimall.product.app;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.merchen.gulimall.product.entity.BrandEntity;
import com.merchen.gulimall.product.vo.BrandVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.merchen.gulimall.product.entity.CategoryBrandRelationEntity;
import com.merchen.gulimall.product.service.CategoryBrandRelationService;
import com.merchen.common.utils.PageUtils;
import com.merchen.common.utils.R;


/**
 * 品牌分类关联
 *
 * @author ChenZhongBo
 * @email 961208477@qq.com
 * @date 2022-06-04 20:05:18
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    /**
     * 获取分类关联的品牌
     * /product/categorybrandrelation/brands/list
     */
    @GetMapping("/brands/list")
    public R getAllBrands(@RequestParam(value = "catId", required = true) Long catId) {

        List<BrandEntity> list = categoryBrandRelationService.getList(catId);
        if(list!=null&&list.size()>0){
            List<BrandVO> brandVOS = list.stream().map((item) -> {
                BrandVO brandVO = new BrandVO();
                brandVO.setBrandId(item.getBrandId());
                brandVO.setBrandName(item.getName());
                return brandVO;
            }).collect(Collectors.toList());
            return R.ok().put("data", brandVOS);
        }
        return R.ok().put("data", null);
    }

    /**
     * 列表
     */
    @RequestMapping("/catelog/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = categoryBrandRelationService.queryPage(params);
        List<?> list = page.getList();
        return R.ok().put("data", list);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation) {
        categoryBrandRelationService.saveDetail(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation) {
        categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
