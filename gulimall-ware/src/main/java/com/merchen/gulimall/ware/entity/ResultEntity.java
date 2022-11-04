package com.merchen.gulimall.ware.entity;

import lombok.Data;

/**
 * @author MrChen
 * @create 2022-07-14 19:51
 */
@Data
public class ResultEntity<T> {
    private T data;
    private String message;
    private Integer code;

    public T getData() {
        return data;
    }

    public ResultEntity setData(T data) {
        this.data = data;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public ResultEntity(String message, Integer code) {
        this.message = message;
        this.code = code;
    }
    public static ResultEntity OK() {
        return new ResultEntity<>("success",200);
    }
    public static ResultEntity OK(String message, Integer code){
        return new ResultEntity(message,code);
    }
    public static ResultEntity error(){
        return new ResultEntity<>("error",400);
    }
    public static ResultEntity error(String message, Integer code){
        return new ResultEntity(message,code);
    }

}
