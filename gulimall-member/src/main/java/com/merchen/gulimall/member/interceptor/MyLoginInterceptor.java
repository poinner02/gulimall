package com.merchen.gulimall.member.interceptor;

import com.merchen.common.constant.AuthServiceConstant;
import com.merchen.common.vo.MemberResponVo;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author MrChen
 * @create 2022-09-01 19:48
 */
@Component
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
        boolean match = new AntPathMatcher().match("/member/**", requestURI);
        if(match){
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
