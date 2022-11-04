package com.merchen.gulimall.auth.vo;

import io.swagger.annotations.Api;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

import javax.validation.constraints.Pattern;

/**
 * @author MrChen
 * @create 2022-08-15 22:45
 */
@Data
public class RegistUserVo {

    @NotEmpty(message = "用户名字不能为空")
    @Length(message = "长度在6~10", max = 10, min = 6)
    private String username;

    @NotEmpty(message = "密码不能为空")
    @Length(message = "密码必须6~18位~18", max = 18, min = 6)
    private String password;

    @Pattern(regexp = "^[1]([3-9])[0-9]{9}$", message = "手机号格式不正确")
    private String phone;

    @NotEmpty(message = "验证码必须填写")
    private String code;
}
