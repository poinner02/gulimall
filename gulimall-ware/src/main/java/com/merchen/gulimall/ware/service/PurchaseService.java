package com.merchen.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.merchen.common.utils.PageUtils;
import com.merchen.gulimall.ware.entity.PurchaseEntity;
import com.merchen.gulimall.ware.vo.MergePurchaseVO;
import com.merchen.gulimall.ware.vo.PurchaseDoneVO;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author ChenZhongBo
 * @email 961208477@qq.com
 * @date 2022-06-05 19:23:27
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryUnreceivelist(Map<String, Object> params);

    void merge(MergePurchaseVO mergePurchaseVO);

    void receive(List<Long> ids);

    void done(PurchaseDoneVO purchaseDoneVO);
}

