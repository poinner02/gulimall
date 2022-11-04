package com.merchenl.gulimall.cartservice.interceptor;

import com.merchen.common.constant.AuthServiceConstant;
import com.merchen.common.constant.CartConstant;
import com.merchen.common.vo.MemberResponVo;

import com.merchenl.gulimall.cartservice.to.UserInfoTo;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 拦截器
 *
 * @author MrChen
 * @create 2022-08-24 22:42
 */
public class CartInterceptor implements HandlerInterceptor {

    public static ThreadLocal<UserInfoTo> threadLocal = new ThreadLocal<>();

    /**
     * springmvc 设置用户userInfo购物车信息
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从session域中获取用户信息
        MemberResponVo memberResponVo = (MemberResponVo) request.getSession().getAttribute(AuthServiceConstant.LOGIN_USER);
        UserInfoTo userInfoTo = new UserInfoTo();
        //①是登录的状态
        if (memberResponVo != null) {
            //登录情况下设置id
            userInfoTo.setUserId(memberResponVo.getId());
        }
        //没有登录的状态
        //②查看浏览器是否有cookie信息
        Cookie[] cookies = request.getCookies();
        if(cookies!=null&&cookies.length>0){
            List<Cookie> collect = Arrays.stream(request.getCookies()).filter(cookie -> {
                return CartConstant.TEMP_CART_USER_KEY.equals(cookie.getName());
            }).collect(Collectors.toList());
            //有cookie则设置cookie信息
            if (collect != null && collect.size() > 0) {
                Cookie cookie = collect.get(0);
                userInfoTo.setUserKey(cookie.getValue());
                userInfoTo.setTempUser(true);//浏览器是否有痕迹，是则设置过期时间标志位
            }
        }
        //承接上一步② 如果浏览器没有登录且没有cookie的痕迹，设置临时的cookie信息存入UserInfo
        if(StringUtils.isEmpty(userInfoTo.getUserKey())){
            String userKey = UUID.randomUUID().toString().replace("-","");
            userInfoTo.setUserKey(userKey);
        }
        //最总结果都会设置2个属性值
        threadLocal.set(userInfoTo);
        return true;
    }

    //方法执行后，设置浏览器cookie临时用户信息
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserInfoTo userInfoTo = threadLocal.get();
        //浏览器没有存在临时用户信息
        if(!userInfoTo.getTempUser()){
            Cookie cookie = new Cookie(CartConstant.TEMP_CART_USER_KEY, userInfoTo.getUserKey());
            cookie.setMaxAge(CartConstant.TEMP_CART_USER_TIMEOUT);//设置过期时间1个月
            cookie.setDomain("gulimall.com");//设置作用域
            response.addCookie(cookie);
        }
    }
}
