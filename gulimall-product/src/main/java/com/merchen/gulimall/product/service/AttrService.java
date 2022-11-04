package com.merchen.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.merchen.common.utils.PageUtils;
import com.merchen.gulimall.product.entity.AttrEntity;
import com.merchen.gulimall.product.vo.AttrVO;
import com.merchen.gulimall.product.vo.SpuAttrVO;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author ChenZhongBo
 * @email 961208477@qq.com
 * @date 2022-06-04 20:05:18
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params, Long cateLogId, String type);

    AttrVO getDetail(Long attrId);

    void saveDetail(AttrVO attr);

    void updateDetails(AttrVO attrVO);

    List<AttrEntity> getRelationAttr(Long attrGroupId);

    void updateSpuAttr(List<SpuAttrVO> spuAttrVOS, Long spuId);

    List<Long> getFilerIds(List<Long> nofilerIds);
}

