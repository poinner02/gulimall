package com.merchenl.gulimall.cartservice.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.merchen.common.utils.R;
import com.merchen.common.vo.Cart;
import com.merchen.common.vo.CartItem;
import com.merchenl.gulimall.cartservice.feignService.ProductFeignService;
import com.merchenl.gulimall.cartservice.interceptor.CartInterceptor;
import com.merchenl.gulimall.cartservice.service.CartService;
import com.merchenl.gulimall.cartservice.to.UserInfoTo;
import com.merchenl.gulimall.cartservice.vo.SkuInfoEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * @author MrChen
 * @create 2022-08-24 22:32
 */
@Slf4j
@Service
public class CartServiceImp implements CartService {
    private String CART_REDIS_KEY_PREFIX = "gulimall:cart:";

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private ThreadPoolExecutor executor;

    @Override
    public Cart SimplefyGetCart() {
        //获取当前线程绑定的redis hash操作
        BoundHashOperations cartOps = getCartOps();
        //获取当前线程购物车用户entity
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        Cart cart = new Cart();
        //用户登录情况下，合并离线，并且清空离线购物车
        if (userInfoTo.getUserId() != null) {
            //获取离线购物车数据
            List<Object> values = redisTemplate.opsForHash().values(CART_REDIS_KEY_PREFIX + userInfoTo.getUserKey());
            if (values.size() > 0 && values != null) {
                for (Object cartItemJson : values) {
                    CartItem cartItem = JSON.parseObject((String) cartItemJson, CartItem.class);
                    //合并
                    if (cartOps.hasKey(cartItem.getSkuId().toString())) {
                        String json = (String) cartOps.get(cartItem.getSkuId().toString());
                        CartItem cartItem1 = JSON.parseObject(json, CartItem.class);
                        cartItem1.setCount(cartItem1.getCount() + cartItem.getCount());
                        cartOps.put(cartItem.getSkuId().toString(), JSON.toJSONString(cartItem1));
                    }
                }
            }
            //获取要的数据类型
            List list = cartOps.values();
            if (list != null && list.size() > 0) {
                Collection collect = (Collection) list.stream().map(json -> {
                    CartItem cartItem = JSON.parseObject((String) json, CartItem.class);
                    return cartItem;
                }).collect(Collectors.toList());
                cart.setItems(new ArrayList<>(collect));
            }
            //清空离线购物车
            clearTempCart(userInfoTo);
        } else {
            //没有登录情况下，直接返回离线购物车Cart
            //获取redis中离线的cartItem json 数据
            List list = cartOps.values();
            if (list != null && list.size() > 0) {
                Collection<CartItem> collect = (Collection) list.stream().map(cartItemJson -> {
                    CartItem cartItem = JSON.parseObject((String) cartItemJson, CartItem.class);
                    return cartItem;
                }).collect(Collectors.toList());
                cart.setItems(new ArrayList<>(collect));
            }
        }
        return cart;
    }

    @Override
    public Cart delectCartItem(Long skuId) {
        return clearTempCart(skuId);

    }

    @Override
    public void changeChecked(Integer checked, Long skuId) {
        BoundHashOperations cartOps = getCartOps();
        if (cartOps.hasKey(skuId.toString())) {
            String json = (String) cartOps.get(skuId.toString());
            CartItem cartItem = JSON.parseObject(json, CartItem.class);
            cartItem.setCheck(checked == 1);
            cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));
        }
    }

    /**
     * 实时获取购物车
     * @return
     */
    @Override
    public List<CartItem> getCartItems() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if(!StringUtils.isEmpty(userInfoTo.getUserId())){
            //从当前线程获取用户的id
            String key = "gulimall:cart:" + userInfoTo.getUserId();
            //获取购物车集合
            List<Object> values = redisTemplate.opsForHash().values(key);
            if(values !=null && values.size()>0) {
                //初步获取购物车集合
                List<CartItem> collect = values.stream().map(obj -> {
                    String json = (String) obj;
                    CartItem cartItem = JSON.parseObject(json, CartItem.class);
                    return cartItem;
                }).collect(Collectors.toList());
                //过滤选中的商品和获取实时价格
                List<CartItem> result = collect.stream().filter(item -> {
                    return item.isCheck();
                }).map(i -> {
                    //实时获取价格根据pro服务
                    Long skuId = i.getSkuId();
                    R info = productFeignService.info(skuId);
                    if (info.getCode() == 0) {
                        SkuInfoEntity data = info.getData("skuInfo", new TypeReference<SkuInfoEntity>() {
                        });
                        i.setPrice(data.getPrice());
                    }
                    return i;
                }).collect(Collectors.toList());
                return result;
            }
            return null;
        }else{
            return null;
        }
    }

    private Cart clearTempCart(Long skuId) {
        BoundHashOperations cartOps = getCartOps();
        cartOps.delete(skuId.toString());
        Cart cart = SimplefyGetCart();
        return cart;
    }


    @Override
    public CartItem addtoCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {

        BoundHashOperations operations = getCartOps();
        CartItem cartItem = new CartItem();
        //如果购物车中有数据，则对count做累加
        if (!operations.hasKey(skuId.toString())) {
            //远程获取skuIntity
            R info = productFeignService.info(skuId);
            //属性对拷
            CartItem finalCartItem = cartItem;
            CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
                SkuInfoEntity skuInfo = info.getData("skuInfo", new TypeReference<SkuInfoEntity>() {
                });
                finalCartItem.setImg(skuInfo.getSkuDefaultImg());
                finalCartItem.setCount(num);
                finalCartItem.setPrice(skuInfo.getPrice());
                finalCartItem.setTitel(skuInfo.getSkuTitle());
                finalCartItem.setSkuId(skuId);
            }, executor);
            CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
                List<String> skuAttrValues = productFeignService.getSkuAttrValues(skuId);
                finalCartItem.setAttrs(skuAttrValues);
            }, executor);
            CompletableFuture.allOf(future1, future2).get();
            operations.put(skuId.toString(), JSON.toJSONString(finalCartItem));
            return finalCartItem;
        } else {
            String json = (String) operations.get(skuId.toString());
            cartItem = JSON.parseObject(json, CartItem.class);
            cartItem.setCount(cartItem.getCount() + num);
            operations.put(skuId.toString(), JSON.toJSONString(cartItem));
            return cartItem;
        }


    }

    @Override
    public CartItem getCartItem(Long skuId) {
        String json = (String) getCartOps().get(skuId.toString());
        CartItem cartItem = JSON.parseObject(json, new TypeReference<CartItem>() {
        });
        return cartItem;
    }

    /**
     * 获取购物车详情
     * 没有登录获取 离线
     * 有登录获取登录
     * 如果是登录合并购物车，并且删除离线redis数据
     * 如果是离线则不删除
     *
     * @return
     */
    @Override
    public Cart getCart() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        Cart cart = new Cart();
        List<Object> loginCartValues = redisTemplate.opsForHash().values(CART_REDIS_KEY_PREFIX + userInfoTo.getUserId());//登录
        List<Object> lineoffCartValues = redisTemplate.opsForHash().values(CART_REDIS_KEY_PREFIX + userInfoTo.getUserKey());//离线
        //登陆后的购物车
        if (userInfoTo.getUserId() != null) {

            HashMap<String, String> map = new HashMap<>();
            //登录购物车数据存入map中
            for (Object value : loginCartValues) {
                CartItem cartItem = JSON.parseObject((String) value, CartItem.class);
                map.put(cartItem.getSkuId().toString(), JSON.toJSONString(cartItem));
            }
            //离线购物车合并到登录账号的购物车中
            for (Object value : lineoffCartValues) {
                CartItem cartItem = JSON.parseObject((String) value, CartItem.class);
                if (map.containsKey(cartItem.getSkuId().toString())) {
                    //更新数据
                    String json = map.get(cartItem.getSkuId().toString());
                    CartItem cartItem1 = JSON.parseObject(json, CartItem.class);
                    cartItem1.setCount(cartItem1.getCount() + cartItem.getCount());
                    map.put(cartItem1.getSkuId().toString(), JSON.toJSONString(cartItem1));
                } else {
                    //插入新的数据
                    map.put(cartItem.getSkuId().toString(), JSON.toJSONString(cartItem));
                }
            }
            Collection<String> carts = map.values();
            if (carts != null && carts.size() > 0) {
                List<CartItem> collect = carts.stream().map(s -> {
                    CartItem cartItem = JSON.parseObject(s, CartItem.class);
                    return cartItem;
                }).collect(Collectors.toList());
                cart.setItems(new ArrayList<>(collect));
            }
            //删除离线购物车数据
            clearTempCart(userInfoTo);
            //更新登录购物车数据
            redisTemplate.opsForHash().putAll(CART_REDIS_KEY_PREFIX + userInfoTo.getUserId(), map);
        } else {
            //没有登录，只获取离线数据
            if (lineoffCartValues != null && lineoffCartValues.size() > 0) {
                List<CartItem> collect = lineoffCartValues.stream().map(o -> {
                    CartItem cartItem = JSON.parseObject((String) o, CartItem.class);
                    return cartItem;
                }).collect(Collectors.toList());
                cart.setItems(new ArrayList<>(collect));
            }
        }
        return cart;
    }

    //清空临时购物车
    private void clearTempCart(UserInfoTo userInfoTo) {
        redisTemplate.delete(CART_REDIS_KEY_PREFIX + userInfoTo.getUserKey());
    }

    //todo 跟新数量
    @Override
    public void updateCart(Long skuId, Integer num) {
        BoundHashOperations cartOps = getCartOps();
        if (cartOps.hasKey(skuId.toString())) {
            String json = (String) cartOps.get(skuId.toString());
            CartItem cartItem = JSON.parseObject(json, CartItem.class);
            cartItem.setCount(num);
            cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));
        }
    }

    private BoundHashOperations getCartOps() {
        //获取当前线程的userinfo
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        //区分离线购物车和登录购物车
        String cart_redis_key = "";
        //有先后顺序的
        if (userInfoTo.getUserId() != null) {
            //登录购物车
            cart_redis_key = CART_REDIS_KEY_PREFIX + userInfoTo.getUserId();
        } else {
            //离线购物车
            cart_redis_key = CART_REDIS_KEY_PREFIX + userInfoTo.getUserKey();

        }
        BoundHashOperations<String, Object, Object> stringObjectObjectBoundHashOperations = redisTemplate.boundHashOps(cart_redis_key);
        return stringObjectObjectBoundHashOperations;
    }
}
