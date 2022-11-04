package com.merchen.gulimall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author MrChen
 * @create 2022-06-28 22:29
 */
@Data
public class PurchaseDoneVO {
    /**
     * {
     *    id: 123,//采购单id
     *    items: [{itemId:1,status:4,reason:""}]//完成/失败的需求详情
     * }
     */
    @NotNull
    private Long  id;
    private List<PurchaseReasonVO> items;
}
