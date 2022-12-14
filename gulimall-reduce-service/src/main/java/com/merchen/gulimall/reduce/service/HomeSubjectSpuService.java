package com.merchen.gulimall.reduce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.merchen.common.utils.PageUtils;
import com.merchen.gulimall.reduce.entity.HomeSubjectSpuEntity;

import java.util.Map;

/**
 * δΈι’εε
 *
 * @author ChenZhongBo
 * @email 961208477@qq.com
 * @date 2022-09-02 19:17:23
 */
public interface HomeSubjectSpuService extends IService<HomeSubjectSpuEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

