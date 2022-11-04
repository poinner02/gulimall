package com.merchen.common.exception;

/**
 * @author MrChen
 * @create 2022-06-15 19:33
 */
public enum BizCodeEnume {

    TOO_MANY_RESQUEST(10002,"请求流量过大"),
    UNKNOW_EXCEPTION(10000,"系统未知异常"),
    VALID_EXCEPTION(10001,"参数格式校验失败"),
    PRODUCT_UP_EXCEPTION(11000,"商品上架出现异常");

    private int code;
    private String message;

    BizCodeEnume(int code,String msg){
        this.code = code;
        this.message =msg;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
