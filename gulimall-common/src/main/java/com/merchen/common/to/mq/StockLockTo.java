package com.merchen.common.to.mq;

import lombok.Data;


/**
 * @author MrChen
 * @create 2022-09-19 20:33
 */
@Data
public class StockLockTo {
    private Long id;//工作单id
    private StockDetailTo detail;//工作详情单id
}
