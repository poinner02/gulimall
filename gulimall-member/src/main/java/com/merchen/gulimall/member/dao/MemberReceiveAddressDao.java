package com.merchen.gulimall.member.dao;

import com.merchen.gulimall.member.entity.MemberReceiveAddressEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 会员收货地址
 * 
 * @author ChenZhongBo
 * @email 961208477@qq.com
 * @date 2022-06-05 19:10:51
 */
@Mapper
public interface MemberReceiveAddressDao extends BaseMapper<MemberReceiveAddressEntity> {

    List<MemberReceiveAddressEntity> selectListById(Long memberId);
}
