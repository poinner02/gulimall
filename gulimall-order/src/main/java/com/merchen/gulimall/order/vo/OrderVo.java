package com.merchen.gulimall.order.vo;

import com.merchen.common.vo.CartItem;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author MrChen
 * @create 2022-09-02 11:34
 */

public class OrderVo {
    /**
     *收获地址
     */
    List<MemberReceiveAddressEntity> adresses;
    /**
     *购物车中的订单
     */
    List<CartItem> cartItemList;

    /**
     * 优惠
     */
    BigDecimal reduce =  new BigDecimal(""+0);

    /**
     *  总商品金额
     */
    BigDecimal totalAmount;

    /**
     * 应付总额
     */
    BigDecimal payAmount;

    /**
     * 总件数
     * @return
     */
    Integer count;

    /**
     * 是否有库存
     * @return
     */
    @Getter @Setter
    Map<Long,Boolean> hastcokMap;

    /**
     * 订单提交防重令牌
     */
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<MemberReceiveAddressEntity> getAdresses() {
        return adresses;
    }

    public void setAdresses(List<MemberReceiveAddressEntity> adresses) {
        this.adresses = adresses;
    }

    public List<CartItem> getCartItemList() {
        return cartItemList;
    }

    public void setCartItemList(List<CartItem> cartItemList) {
        this.cartItemList = cartItemList;
    }

    //todo
    public BigDecimal getTotalAmount() {
        BigDecimal totalAmount = new BigDecimal(""+0);
        for (CartItem cartItem : cartItemList) {
            totalAmount  = totalAmount.add(cartItem.getTotalPrice());
        }
        return totalAmount;
    }

    //todo
    public BigDecimal getPayAmount() {
        BigDecimal totalAmount = getTotalAmount();
        BigDecimal byReduce = totalAmount.subtract(reduce);
        return byReduce;
    }

    public Integer getCount() {
        Integer count = 0;
        for (CartItem cartItem : cartItemList) {
            count = count  + cartItem.getCount()
            ;
        }
        return count;
    }

    public BigDecimal getReduce() {
        return reduce;
    }

    public void setReduce(BigDecimal reduce) {
        this.reduce = reduce;
    }
}
