package com.merchen.gulimall.product.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.merchen.common.constant.AuthServiceConstant;
import com.merchen.common.to.MemberTO;
import com.merchen.common.to.SecKillSkuRedisTo;
import com.merchen.common.utils.JwtUtils;
import com.merchen.common.utils.R;
import com.merchen.common.vo.MemberResponVo;
import com.merchen.gulimall.product.entity.CategoryEntity;
import com.merchen.gulimall.product.feign.MemberFeignService;
import com.merchen.gulimall.product.service.CategoryService;
import com.merchen.gulimall.product.vo.CateLogory2VO;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

/**
 * @author MrChen
 * @create 2022-07-14 22:04
 */
@Controller
public class IndexController {


    @Autowired
    private RedissonClient redisson;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private MemberFeignService memberFeignService;


    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     *
     * 处理秒数据
     * @param request
     * @param token
     * @param session
     * @return
     */
    @GetMapping({"/", "/index.html"})
    public String indexPage(HttpServletRequest request,
                            @RequestParam(value = "sso_token",required = false)String token,
                            HttpSession session) {
//        if(!StringUtils.isEmpty(token)){
//            //之前有人登录过
//            String json = redisTemplate.opsForValue().get(token);
//            MemberResponVo memberResponVo = JSON.parseObject(json, new TypeReference<MemberResponVo>() {
//            });
//            session.setAttribute(AuthServiceConstant.LOGIN_USER,memberResponVo);
//        }
//        if(session.getAttribute(AuthServiceConstant.LOGIN_USER)==null){
//            return "redirect:http://auth.gulimall.com/login.html";
//        }
        //登陆后获取其他的信息
        //1 查出1级分类
        List<CategoryEntity> list = categoryService.getOneLeveL();
        request.setAttribute("oneLevelList", list);
//        //秒杀数据获取
//        if(redisTemplate.hasKey("seckill:skus")){
//            BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps("seckill:skus");
//            List<Object> values = ops.values();
//            List<SecKillSkuRedisTo> collect = values.stream().map(item -> {
//                SecKillSkuRedisTo secKillSkuRedisTo = JSON.parseObject((String) item, new TypeReference<SecKillSkuRedisTo>() {
//                });
//                return secKillSkuRedisTo;
//            }).collect(Collectors.toList());
//            request.setAttribute("seckills",collect);
//        }
        return "index";
    }


    @ResponseBody
    @GetMapping("/index/json/catalog.json")
    public Object getcateGoryJson() {

        //获取品牌分类tree
        Map<String, List<CateLogory2VO>> map = categoryService.getcateGoryJson();
        return map;
    }

    //redisson分布式锁测试
    @GetMapping("/hello")
    public String hello() throws InterruptedException {
        RLock lock = redisson.getLock("anyLock");
        lock.lock();
        try {
            System.out.println("加锁成功，执行业务.."+Thread.currentThread().getId());
            Thread.sleep(30000);
        } catch (Exception e){

        }finally {
            System.out.println("释放锁成功.." + Thread.currentThread().getId());
            lock.unlock();
        }
        return "hello";

    }
}
