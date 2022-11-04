package com.merchen.gulimall.order.interceptor;

import com.merchen.common.constant.AuthServiceConstant;
import com.merchen.common.utils.R;
import com.merchen.common.vo.MemberResponVo;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author MrChen
 * @create 2022-09-01 19:48
 */
public class MyLoginInterceptor implements HandlerInterceptor {

    /**
     * 线程共享MemberResponVo
     */
    public static ThreadLocal<MemberResponVo> loginUser = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        /**
         * 解锁库存ware调用order放行
         */
        String requestURI = request.getRequestURI();
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        boolean match = antPathMatcher.match("/order/order/status/**", requestURI);
        boolean match1 = antPathMatcher.match("/payed/**", requestURI);
        boolean match2 = antPathMatcher.match("/order/order/list/**", requestURI);
        if(match || match1 || match2){
            return true;
        }
        /**
         * 每个请求都拦截
         */
        MemberResponVo memberResponVo = null;
        Object attribute = request.getSession().getAttribute(AuthServiceConstant.LOGIN_USER);
        if (attribute != null) {
            memberResponVo = (MemberResponVo) attribute;
            loginUser.set(memberResponVo);
            return true;
        }
        response.sendRedirect("http://auth.gulimall.com/login.html");
        return false;
    }
}
