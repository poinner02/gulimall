package com.merchen.common.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 整个购物车
 * @author MrChen
 * @create 2022-08-24 21:41
 */

public class Cart implements Serializable {
    private Integer countNum; //商品数量
    private List<CartItem> items;
    private Integer countType;//商品类型数量
    private BigDecimal totalAmount;//商品总价格
    private BigDecimal reduce = new BigDecimal(""+0);//优惠价格

    public Integer getCountNum() {
        int countNum = 0;
        if(items != null && items.size() >0){
            for (CartItem item : items) {
                countNum+=item.getCount();
            }
        }
        return countNum;
    }


    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public Integer getCountType() {
        int countNum = 0;
        if(items != null && items.size() >0){
            for (CartItem item : items) {
                countNum++;
            }
        }
        return countNum;
    }

    /**
     * 根据CartItem计算总价格
     * @return
     */
    public BigDecimal getTotalAmount() {
        BigDecimal amount = new BigDecimal(""+0);
        if(items != null && items.size() >0){
            for (CartItem item : items) {
                if(item.isCheck()){
                    amount = amount.add(item.getTotalPrice());
                }
            }
        }
        //减去优惠价格
        amount = amount.subtract(reduce);
        return amount;
    }

    public BigDecimal getReduce() {
        return reduce;
    }

    public void setReduce(BigDecimal reduce) {
        this.reduce = reduce;
    }
}
