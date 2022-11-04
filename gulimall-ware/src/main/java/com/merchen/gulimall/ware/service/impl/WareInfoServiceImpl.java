package com.merchen.gulimall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.merchen.common.utils.R;
import com.merchen.gulimall.ware.feign.MemberFeignService;
import com.merchen.gulimall.ware.vo.FareVo;
import com.merchen.gulimall.ware.vo.MemberReceiveAddressEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.merchen.common.utils.PageUtils;
import com.merchen.common.utils.Query;

import com.merchen.gulimall.ware.dao.WareInfoDao;
import com.merchen.gulimall.ware.entity.WareInfoEntity;
import com.merchen.gulimall.ware.service.WareInfoService;
import org.springframework.util.StringUtils;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Autowired
    private MemberFeignService memberFeignService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
//        IPage<WareInfoEntity> page = this.page(
//                new Query<WareInfoEntity>().getPage(params),
//                new QueryWrapper<WareInfoEntity>()
//        );

        IPage<WareInfoEntity> page = new Query<WareInfoEntity>().getPage(params);
        QueryWrapper<WareInfoEntity> wrapper = new QueryWrapper<>();
        String key  = (String) params.get("key");
        //模糊查询
        if(!StringUtils.isEmpty(key)){
            wrapper.and(object->{
                object.eq("id", key)
                        .or()
                        .like("name", key)
                        .or()
                        .like("address", key)
                        .or()
                        .like("areacode", key);
            });
        }
        IPage<WareInfoEntity> wareInfoEntityIPage = this.baseMapper.selectPage(page, wrapper);
        return new PageUtils(wareInfoEntityIPage);
    }

    /**
     * 根据attrid获取运费
     * @param attrId
     * @return
     */
    @Override
    public FareVo fare(Long attrId) {
        //查询远程服务获取用户地址
        R info = memberFeignService.info(attrId);
        if(info.getCode()==0){
            MemberReceiveAddressEntity data = info.getData("memberReceiveAddress",new TypeReference<MemberReceiveAddressEntity>() {
            });
            if(data!=null){
                FareVo fareVo = new FareVo();
                //模拟获取运费13524252325
                String phone = data.getPhone();
                String fare = phone.substring(phone.length()-1, phone.length());
                fareVo.setFare(new BigDecimal(fare));
                fareVo.setMemberReceiveAddressEntity(data);
                return fareVo;
            }
        }
        return null;
    }

}