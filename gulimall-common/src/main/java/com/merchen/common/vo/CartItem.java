package com.merchen.common.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 用户的购物车
 * @author MrChen
 * @create 2022-08-24 21:42
 */
public class CartItem implements Serializable {
    private Long skuId;//商品id
    private boolean check = true;//是否被勾选
    private String img;//商品图片
    private String titel;//商品标题
    private List<String> attrs;//商品属性组合
    private BigDecimal price;//商品单价
    private Integer count;//商品数量
    private BigDecimal totalPrice;//商品小计价格

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public List<String> getAttrs() {
        return attrs;
    }

    public void setAttrs(List<String> attrs) {
        this.attrs = attrs;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    /**
     * 动态计算某个商品的小计价格
     * @return
     */
    public BigDecimal getTotalPrice() {
        return this.price.multiply(new BigDecimal(""+this.count));
    }


}
