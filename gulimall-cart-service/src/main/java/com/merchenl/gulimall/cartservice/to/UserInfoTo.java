package com.merchenl.gulimall.cartservice.to;

import lombok.Data;

/**
 * @author MrChen
 * @create 2022-08-24 22:59
 */
@Data
public class UserInfoTo {
    private  Long userId;
    private String userKey;
    private Boolean tempUser = false;//是否设置了过期时间，false是没有设置，true是设置了
}
