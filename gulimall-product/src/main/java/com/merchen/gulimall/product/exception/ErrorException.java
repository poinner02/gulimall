package com.merchen.gulimall.product.exception;

/**
 * @author MrChen
 * @create 2022-10-10 22:27
 */
public class ErrorException extends RuntimeException{

    private String msg;

    public ErrorException(String message) {
        super(message);
        this.msg = msg;
    }
}
