package com.merchen.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author MrChen
 * @create 2022-08-22 22:13
 */
public class JwtUtils {

    //设置token过期时间
    public static final long EXPIRE = 1000 * 60 * 60 * 24;
    //密钥随便添，或者每个公司项目的密钥自己设置
    public static final String APP_SECRET = "ukc8BDbRigUDaY6pZFfWus2jZWLPHO";
    //生成token字符串方法
    public static String getJwtToken(String id, String nickname){

        String JwtToken = Jwts.builder()
                //设置jwt的头信息 不用改
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                .setSubject("my_project_user")//分类 按照自己的项目
                .setIssuedAt(new Date())//设置token过期时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))

                .claim("id", id) //设置token主题部分，存储用户信息
                .claim("nickname", nickname)

                .signWith(SignatureAlgorithm.HS256, APP_SECRET)
                .compact();

        return JwtToken;
    }

    /**
     * 判断token是否存在与有效
     * @param jwtToken
     * @return
     */
    public static boolean checkToken(String jwtToken) {
        if(StringUtils.isEmpty(jwtToken)) return false;
        try {
            Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 判断token是否存在与有效
     * @param request
     * @return
     */
    public static boolean checkToken(HttpServletRequest request) {
        try {
            String jwtToken = request.getHeader("token");
            if(StringUtils.isEmpty(jwtToken)) return false;
            Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 根据token获取会员id
     * @param request
     * @return
     */
    public static String getMemberIdByJwtToken(HttpServletRequest request) {
//        String jwtToken = request.getHeader("token");
        List<Cookie> collect = Arrays.stream(request.getCookies()).filter(cookie -> {
            return "token".equals(cookie.getName());
        }).collect(Collectors.toList());
        if(collect !=null && collect.size() ==1){
           String jwtToken = collect.get(0).getValue();
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(APP_SECRET).parseClaimsJws(jwtToken);
            Claims claims = claimsJws.getBody();
            return (String)claims.get("id");
        }
//        if(StringUtils.isEmpty(jwtToken))
            return "";

    }
}
