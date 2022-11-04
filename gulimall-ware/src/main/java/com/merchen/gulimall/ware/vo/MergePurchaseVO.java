package com.merchen.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * @author MrChen
 * @create 2022-06-28 17:52
 */
@Data
public class MergePurchaseVO {
    private Long purchaseId;
    private List<Long> items;
}
