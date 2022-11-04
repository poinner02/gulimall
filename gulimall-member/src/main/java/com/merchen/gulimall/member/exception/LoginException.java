package com.merchen.gulimall.member.exception;

/**
 * @author MrChen
 * @create 2022-08-17 22:17
 */
public class LoginException extends  RuntimeException {
    public LoginException() {
        super("用户或者密码错误");
    }
}
