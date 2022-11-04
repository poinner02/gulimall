package com.merchem.gulimall.secskill.interceptor;

import com.merchen.common.constant.AuthServiceConstant;
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

        String requestURI = request.getRequestURI();
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        boolean match = antPathMatcher.match("/kill", requestURI);

        if(match ){
            MemberResponVo memberResponVo = null;
            Object attribute = request.getSession().getAttribute(AuthServiceConstant.LOGIN_USER);
            if (attribute != null) {
                memberResponVo = (MemberResponVo) attribute;
                loginUser.set(memberResponVo);
                return true;
            }
            response.sendRedirect("http://auth.gulimall.com/login.html");
        }
        return true;
    }
}
