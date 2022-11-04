package com.merchen.gulimall.member.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.merchen.common.utils.PageUtils;
import com.merchen.common.utils.Query;

import com.merchen.gulimall.member.dao.MemberLevelDao;
import com.merchen.gulimall.member.entity.MemberLevelEntity;
import com.merchen.gulimall.member.service.MemberLevelService;
import org.springframework.util.StringUtils;


@Service("memberLevelService")
public class MemberLevelServiceImpl extends ServiceImpl<MemberLevelDao, MemberLevelEntity> implements MemberLevelService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberLevelEntity> page = this.page(
                new Query<MemberLevelEntity>().getPage(params),
                new QueryWrapper<MemberLevelEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryDetails(Map<String, Object> params) {

        QueryWrapper<MemberLevelEntity> wrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.eq("id",key).or().like("name", key);
        }
        IPage<MemberLevelEntity> page = new Query<MemberLevelEntity>().getPage(params);
        IPage<MemberLevelEntity> iPage = baseMapper.selectPage(page, wrapper);
        PageUtils pageUtils = new PageUtils(iPage);
        return pageUtils;
    }

}