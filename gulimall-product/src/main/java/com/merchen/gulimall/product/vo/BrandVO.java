package com.merchen.gulimall.product.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;


/**
 * @author MrChen
 * @create 2022-06-20 22:16
 */
@Data
@ApiModel("前端返回数据品牌")
public class BrandVO {
    private Long brandId;
    private String brandName;
}
