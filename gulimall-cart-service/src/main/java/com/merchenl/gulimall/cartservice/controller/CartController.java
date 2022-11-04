package com.merchenl.gulimall.cartservice.controller;


import com.merchen.common.constant.AuthServiceConstant;
import com.merchen.common.utils.R;
import com.merchen.common.vo.Cart;
import com.merchen.common.vo.CartItem;
import com.merchenl.gulimall.cartservice.interceptor.CartInterceptor;
import com.merchenl.gulimall.cartservice.service.CartService;
import com.merchenl.gulimall.cartservice.to.UserInfoTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpSession;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * @author MrChen
 * @create 2022-08-24 22:33
 */
@Slf4j
@Controller
public class CartController {

    @Autowired
    private CartService cartService;


    @ResponseBody
    @GetMapping("/getCartItemList")
    public List<CartItem> getCartItems(){
        List<CartItem> list  = cartService.getCartItems();
        return list;
    }

    @PostMapping("/checkCart")
    public String changeChecked(@RequestParam("isChecked")Integer checked,
                                @RequestParam("skuId")Long skuId){
        cartService.changeChecked(checked,skuId);
        return "redirect:http://cart/gulimall.com/cart.html";

    }

    @GetMapping("/toTrade")
    public String toTradePage(HttpSession session){
        if(session.getAttribute(AuthServiceConstant.LOGIN_USER)==null){
            return "redirect:http://auth.gulimall.com/login.html";
        }else{
            return "hello";
        }
    }

    @ResponseBody
    @GetMapping("/cart/delete")
    public R deleteCartItem(@RequestParam("skuId")Long skuId){
        try {
            Cart cart = cartService.delectCartItem(skuId);
            return R.ok().put("data", cart);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error().put("msg", e.getMessage());
        }
//        return "redirect:http://cart.gulimall.com/cart.html";
    }

    @ResponseBody
    @GetMapping("/update")
    public R updateCount(@RequestParam("skuId")Long skuId,
                         @RequestParam("num")Integer num
                              ){
        try {
            cartService.updateCart(skuId,num);
            return R.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return R.error().put("msg",e.getMessage());
        }

//        return "redirect:http://cart.gulimall.com/cart.html";
    }

    /**
     * brower had a cookie : user-key; identify user cid, and one mount expire
     * 第一次使用购物车功能，都会给一个；临时的用户身份
     * 浏览器以后保存，每次都会带上这个cookie
     * <p>
     * 登录有session
     * 没有按照cookie中的user-key来做
     * 第一次：没有临时用户，创建一个临时用户
     *
     * @param
     * @return
     */
    //已经用filter做了拦截
    @GetMapping("/cart.html")
    public String cartPage(Model model) {
        Cart cart = cartService.SimplefyGetCart();
//        session.setAttribute("cart",cart);
        model.addAttribute("cart",cart);
        return "cartList";
    }

    @GetMapping("/addtoCart")
    public  String addtoCart(
            @RequestParam("skuId")Long skuId,
            @RequestParam("num")Integer num,
            RedirectAttributes attributes) throws ExecutionException, InterruptedException {
        cartService.addtoCart(skuId,num);
        attributes.addAttribute("skuId", skuId);
        return "redirect:http://cart.gulimall.com/cartsuccess.html";
    }
    //解决@GetMapping("/addtoCart")重复提交问题
    @GetMapping("/cartsuccess.html")
    public String getSuccessPage(@RequestParam("skuId")Long skuId,
                                 Model model){
        CartItem cartItem = cartService.getCartItem(skuId);
        model.addAttribute("cartItem",cartItem);
        return "success";
    }

}
