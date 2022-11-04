package com.merchen.gulimall.auth.service.serviceImp;

import com.merchen.common.utils.R;

/**
 * @author MrChen
 * @create 2022-08-16 22:00
 */
public interface RegistService {

    R sendCode(String phone);

    public Boolean checkCode(String phone,String verifyCode);

}
