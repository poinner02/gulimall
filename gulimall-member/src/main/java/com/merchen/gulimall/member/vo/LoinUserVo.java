package com.merchen.gulimall.member.vo;

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

    private String loginAcct;
    private String password;


}
