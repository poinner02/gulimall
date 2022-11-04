package com.merchen.gulimall.auth.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
/**
 * @author MrChen
 * @create 2022-08-17 11:51
 */
@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(RegsitException.class)
    public String toRegistPage(RegsitException e,RedirectAttributes attributes){
        HashMap<String,String> errors = new HashMap<>();
        String message = e.getMessage();
        String[] s = message.split("_");
        errors.put(s[1],s[0]);
        attributes.addFlashAttribute("errors", errors);
        return "redirect:http://auth.gulimall.com/regist.html";
    }

    @ExceptionHandler(LoginException.class)
    public String toLoginPage(LoginException e,RedirectAttributes attributes){
        HashMap<String,String> errors = new HashMap<>();
        errors.put("msg", e.getMessage());
        attributes.addFlashAttribute("errors", errors);
        return "redirect:http://auth.gulimall.com/login.html";
    }
}
