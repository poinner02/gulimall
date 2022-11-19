package com.merchen.gulimall.product.app;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.merchen.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.merchen.gulimall.product.entity.AttrEntity;
import com.merchen.gulimall.product.service.AttrAttrgroupRelationService;
import com.merchen.gulimall.product.service.CategoryService;
import com.merchen.gulimall.product.vo.AttrGruopVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.merchen.gulimall.product.entity.AttrGroupEntity;
import com.merchen.gulimall.product.service.AttrGroupService;
import com.merchen.common.utils.PageUtils;
import com.merchen.common.utils.R;



/**
 * 属性分组
 *
 * @author ChenZhongBo
 * @email 961208477@qq.com
 * @date 2022-06-04 20:05:18
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    /**
     * /product/attrgroup/{catelogId}/withattr
     */
    @GetMapping("{catelogId}/withattr")
    public R getAllAttrBycatelogIdGroup(@PathVariable("catelogId")Long cateId){
        List<AttrGruopVO> list = attrGroupService.getAtttrGroupList(cateId);
        return R.ok().put("data",list);


    }
    /**
     * 获取属性分组里面还没有关联的本分类里面的其他基本属性，方便添加新的关联
     */

    @Transactional
    @PostMapping("/attr/relation")
    public R saveRelation(@RequestBody List<AttrAttrgroupRelationEntity> list){
        attrAttrgroupRelationService.saveBatch(list);
        return R.ok();

    }

    @RequestMapping("/{attrGroupId}/noattr/relation")
    public R getNoAttrAndAttrGroupRelation(@PathVariable("attrGroupId")Long attrGroupId,@RequestParam Map<String,Object> params){
        PageUtils page = attrGroupService.getNoAttrAndAttrGroupRelation(attrGroupId,params);
        return R.ok().put("page",page);
    }

    /**
     * 新建关联
     * /product/attrgroup/" + this.attrGroupId + "/noattr/relation
     */
    @RequestMapping("{attrGroupId}/attr/relation")
    public R getAttrAndAttrGroupRelation(@PathVariable("attrGroupId")Long attrGroupId){

        List<AttrEntity> list =  attrGroupService.getReationDetails(attrGroupId);
        return R.ok().put("data",list);

    }

    /**
     * 列表
     */
    @RequestMapping("/list/{catelogId}")
    public R list(@RequestParam Map<String, Object> params,@PathVariable("catelogId") Long catelogId){
//        PageUtils page = attrGroupService.queryPage(params);
        PageUtils page = attrGroupService.queryPage(params,catelogId);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);

		//获取catelogPath
        Long[] catelogPath = categoryService.getCategoryPath(attrGroup.getCatelogId());
        Collections.reverse(Arrays.asList(catelogPath));
        attrGroup.setCatelogPath(catelogPath);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }


    /**
     * 删除属性与分组的关联关系
     * /product/attrgroup/attr/relation/delete
     */
    @PostMapping("/attr/relation/delete")
    public R delRelation(@RequestBody List<AttrAttrgroupRelationEntity> list){


        for (AttrAttrgroupRelationEntity entity : list) {
            QueryWrapper<AttrAttrgroupRelationEntity> wrapper = new QueryWrapper<>();
            wrapper.eq("attr_id", entity.getAttrId()).eq("attr_group_id", entity.getAttrGroupId());
            attrAttrgroupRelationService.remove(wrapper);
        }
        return R.ok();
    }
}
