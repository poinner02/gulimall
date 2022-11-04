package com.merchen.gulimall.coupon.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.merchen.gulimall.coupon.entity.CouponEntity;
import com.merchen.gulimall.coupon.service.CouponService;
import com.merchen.common.utils.PageUtils;
import com.merchen.common.utils.R;



/**
 * 优惠券信息
 *
 * @author ChenZhongBo
 * @email 961208477@qq.com
 * @date 2022-06-05 18:08:58
 */
@RestController
@RequestMapping("coupon/coupon")
@RefreshScope
public class CouponController {

    //todo
    @Value("${user.name}")
    String name;
    //todo
    @Value("${user.password}")
    String password;

    @Autowired
    private CouponService couponService;

    //todo
    @RequestMapping("/test")
    public R test(){
        return R.ok().put("username", name).put("password", password);
    }
    //todo
    @RequestMapping("/remote/list")
    public R list(){
//        PageUtils page = couponService.queryPage(params);

        return R.ok().put("page", "remote-member-coupon");
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = couponService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		CouponEntity coupon = couponService.getById(id);

        return R.ok().put("coupon", coupon);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody CouponEntity coupon){
		couponService.save(coupon);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody CouponEntity coupon){
		couponService.updateById(coupon);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		couponService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
