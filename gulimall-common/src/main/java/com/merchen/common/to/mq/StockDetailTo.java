package com.merchen.common.to.mq;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author MrChen
 * @create 2022-09-19 21:55
 */
@Data
public class StockDetailTo {
    private Long id;
    /**
     * sku_id
     */
    private Long skuId;
    /**
     * sku_name
     */
    private String skuName;
    /**
     * 购买个数
     */
    private Integer skuNum;
    /**
     * 工作单id
     */
    private Long taskId;

    /**
     * 仓库id
     */
    private Long wareId;

    /**
     * 1-锁定 2-解锁 3-扣减
     */
    private Integer lockStatus;
}
