package com.merchen.gulimall.weixinservice.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * @author MrChen
 * @create 2022-08-17 22:06
 */
@Data
public class LoinUserVo implements Serializable {
    @NotEmpty(message = "用户名字不能为空")
    @Length(message = "长度在6~10", max = 10, min = 6)
    private String loginAcct;

    @NotEmpty(message = "密码不能为空")
    @Length(message = "密码必须6~18位~18", max = 18, min = 6)
    private String password;


}
