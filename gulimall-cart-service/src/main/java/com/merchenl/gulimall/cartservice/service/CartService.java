package com.merchenl.gulimall.cartservice.service;


import com.merchen.common.vo.Cart;
import com.merchen.common.vo.CartItem;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author MrChen
 * @create 2022-08-24 22:31
 */
public interface CartService {
    CartItem addtoCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;

    CartItem getCartItem(Long skuId);

    Cart getCart();

    void updateCart(Long skuId, Integer num);

    Cart SimplefyGetCart();

    Cart delectCartItem(Long skuId);

    void changeChecked(Integer checked, Long skuId);

    List<CartItem> getCartItems();
}
