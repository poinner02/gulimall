package com.merchen.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.merchen.common.to.MemberTO;
import com.merchen.common.utils.PageUtils;
import com.merchen.gulimall.member.entity.MemberEntity;
import com.merchen.gulimall.member.vo.LoinUserVo;
import com.merchen.gulimall.member.vo.RegistUserVo;
import com.merchen.gulimall.member.vo.SocialUser;

import java.util.List;
import java.util.Map;

/**
 * 会员
 *
 * @author ChenZhongBo
 * @email 961208477@qq.com
 * @date 2022-06-05 19:10:51
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<MemberTO> getListaByName(String name);

    void regist(RegistUserVo user);

    MemberEntity checkLoginAccount(LoinUserVo user);

    MemberEntity socialLogin(SocialUser socialUser);

    MemberEntity socialLogin(String access_token, String openid);
}

