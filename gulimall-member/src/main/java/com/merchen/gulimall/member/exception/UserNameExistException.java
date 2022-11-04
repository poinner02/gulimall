package com.merchen.gulimall.member.exception;

import com.merchen.common.utils.R;

/**
 * @author MrChen
 * @create 2022-08-17 21:10
 */
public class UserNameExistException extends RuntimeException {
    public UserNameExistException() {
        super("用户名字存在");
    }
}
