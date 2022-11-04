package com.merchen.gulimall.product.vo;

import com.merchen.gulimall.product.entity.AttrEntity;
import lombok.Data;

import java.util.List;

/**
 * @author MrChen
 * @create 2022-06-21 20:56
 */
@Data
public class AttrGruopVO {
    /**
     * "attrGroupId": 1,
     * 		"attrGroupName": "主体",
     * 		"sort": 0,
     * 		"descript": "主体",
     * 		"icon": "dd",
     * 		"catelogId": 225,
     * 		"attrs
     */

    private Long attrGroupId;
    private String attrGroupName;
    private Integer sort;
    private String descript;
    private String icon;
    private Long catelogId;
    private List<AttrEntity> attrs;
    /**
     * "attrId": 7,
     * 			"attrName": "入网型号",
     * 			"searchType": 1,
     * 			"valueType": 0,
     * 			"icon": "xxx",
     * 			"valueSelect": "aaa;bb",
     * 			"attrType": 1,
     * 			"enable": 1,
     * 			"catelogId": 225,
     * 			"showDesc": 1,
     * 			"attrGroupId": null
     */

}
