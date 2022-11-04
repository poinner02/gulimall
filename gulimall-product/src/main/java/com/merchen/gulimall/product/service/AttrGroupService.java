package com.merchen.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.merchen.common.utils.PageUtils;
import com.merchen.gulimall.product.entity.AttrEntity;
import com.merchen.gulimall.product.entity.AttrGroupEntity;
import com.merchen.gulimall.product.vo.AttrGruopVO;
import com.merchen.gulimall.product.vo.SkuItemVo;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author ChenZhongBo
 * @email 961208477@qq.com
 * @date 2022-06-04 20:05:18
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

     List<AttrEntity> getReationDetails(Long attrGroupId) ;

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, Long catelogId);

    PageUtils getNoAttrAndAttrGroupRelation(Long attrGroupId, Map<String, Object> params);

    List<AttrGruopVO> getAtttrGroupList(Long cateId);

    List<SkuItemVo.SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuIdAndCateGoryId(Long spuId,Long categoryId);


}

