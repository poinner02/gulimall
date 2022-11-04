package com.merchen.gulimall.auth.exception;

/**
 * @author MrChen
 * @create 2022-08-17 22:22
 */
public class LoginException extends  RuntimeException{
    public LoginException(String msg) {
        super(msg);
    }
}
