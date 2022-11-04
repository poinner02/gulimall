package com.merchen.gulimall.auth.exception;


/**
 * @author MrChen
 * @create 2022-08-17 20:12
 */
public class RegsitException extends Exception{

    public RegsitException(String message,String code) {
        super(message+"_"+code);
    }
}
