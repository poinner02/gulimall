package com.merchen.gulimall.ware.vo;

import lombok.Data;

/**
 * @author MrChen
 * @create 2022-06-28 22:31
 */
@Data
public class PurchaseReasonVO {
    /**
     *  items: [{itemId:1,status:4,reason:""}]//完成/失败的需求详情
     */
    private Long  itemId;
    private Integer status;
    private String reason;
}
