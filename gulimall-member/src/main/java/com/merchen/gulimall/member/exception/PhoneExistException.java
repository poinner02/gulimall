package com.merchen.gulimall.member.exception;

/**
 * @author MrChen
 * @create 2022-08-17 21:11
 */
public class PhoneExistException extends  RuntimeException{
    public PhoneExistException() {
        super("手机号码存在");
    }
}
