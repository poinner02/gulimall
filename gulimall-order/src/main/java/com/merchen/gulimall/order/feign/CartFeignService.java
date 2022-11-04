package com.merchen.gulimall.order.feign;

import com.merchen.common.vo.CartItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author MrChen
 * @create 2022-09-05 22:47
 */
@Component
@FeignClient("gulimall-cart-service")
public interface CartFeignService {

    @GetMapping("/getCartItemList")
    public List<CartItem> getCartItems();
}
