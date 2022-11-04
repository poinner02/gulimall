package com.merchen.gulimall.product.exception;

import com.merchen.common.exception.BizCodeEnume;
import com.merchen.common.utils.R;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author MrChen
 * @create 2022-06-14 22:56
 */

//@RestControllerAdvice(basePackages = "com.merchen.gulimall.product.controller")
@ControllerAdvice(basePackages = "com.merchen.gulimall.product.controller")
public class ExceptionController {
    //jsr303参数校验错误全局异常
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R getGlobalException(MethodArgumentNotValidException e) {

        Map<String,String> map = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach((item) -> {
            String message = item.getDefaultMessage();
            String field = item.getField();
            map.put(field,message);
        });
        return R.error(BizCodeEnume.VALID_EXCEPTION.getCode(), BizCodeEnume.VALID_EXCEPTION.getMessage()).put("data",map);
    }

    @ExceptionHandler(RuntimeException.class)
    public String exception(){
        return "error";
    }
}
