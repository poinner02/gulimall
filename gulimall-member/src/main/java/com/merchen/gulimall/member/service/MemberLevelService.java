package com.merchen.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.merchen.common.utils.PageUtils;
import com.merchen.gulimall.member.entity.MemberLevelEntity;

import java.util.Map;

/**
 * 会员等级
 *
 * @author ChenZhongBo
 * @email 961208477@qq.com
 * @date 2022-06-05 19:10:51
 */
public interface MemberLevelService extends IService<MemberLevelEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryDetails(Map<String, Object> params);
}

