package com.merchen.gulimall.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.merchen.common.to.MemberTO;


import com.merchen.common.utils.HttpUtils;
import com.merchen.gulimall.member.dao.MemberLevelDao;
import com.merchen.gulimall.member.entity.MemberLevelEntity;
import com.merchen.gulimall.member.exception.LoginException;
import com.merchen.gulimall.member.exception.PhoneExistException;
import com.merchen.gulimall.member.exception.UserNameExistException;
import com.merchen.gulimall.member.vo.LoinUserVo;
import com.merchen.gulimall.member.vo.RegistUserVo;

import com.merchen.gulimall.member.vo.SocialUser;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.merchen.common.utils.PageUtils;
import com.merchen.common.utils.Query;

import com.merchen.gulimall.member.dao.MemberDao;
import com.merchen.gulimall.member.entity.MemberEntity;
import com.merchen.gulimall.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    private MemberLevelDao memberLevelDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<MemberTO> getListaByName(String name) {
        List<MemberEntity> list = this.baseMapper.selectList(new QueryWrapper<MemberEntity>().eq("username", name));
        List<MemberTO> collect = list.stream().map(item -> {
            MemberTO member = new MemberTO();
            BeanUtils.copyProperties(item, member);
            return member;
        }).collect(Collectors.toList());
        return collect;
    }

    @Override
    public void regist(RegistUserVo user) {
        //对手机和名字做唯一性校验
        checkUserName(user.getUsername());
        checkPhone(user.getPhone());

        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setUsername(user.getUsername());
        memberEntity.setMobile(user.getPhone());
        Integer status = 1;//默认是1
        MemberLevelEntity levelEntity = memberLevelDao.selectLeveEntity(status);
        memberEntity.setLevelId(levelEntity.getId());
        //加密
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        //加密后的密码
        String encode = passwordEncoder.encode(user.getPassword());
        memberEntity.setPassword(encode);
        memberEntity.setCreateTime(new Date());
        this.baseMapper.insert(memberEntity);

    }

    @Override
    public MemberEntity checkLoginAccount(LoinUserVo user) {
        //密码校验
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        QueryWrapper<MemberEntity> wrapper = new QueryWrapper<MemberEntity>().eq("username", user.getLoginAcct()).or().eq("mobile", user.getLoginAcct());
        MemberEntity memberEntity = this.baseMapper.selectOne(wrapper);
        if (memberEntity != null) {
            String printPassword = user.getPassword();
            String databaseUserPassword = memberEntity.getPassword();

            if (!passwordEncoder.matches(printPassword, databaseUserPassword)) {
                throw new LoginException();
            }else{
                return memberEntity;
            }
        } else {
            throw new LoginException();
        }

    }

    @Override
    public MemberEntity socialLogin(SocialUser socialUser) {
        //查询db中有社交关联账号
        MemberEntity memberEntity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("uid", socialUser.getUid()));
        if (memberEntity != null) {
            //根据id更新数据 access_token和ExpiresIn
            MemberEntity update = new MemberEntity();
            update.setId(memberEntity.getId());
            update.setAccessToken(socialUser.getAccess_token());
            update.setExpiresIn(socialUser.getExpires_in());
            baseMapper.updateById(update);
            //返回最新entity
            memberEntity.setAccessToken(socialUser.getAccess_token());
            memberEntity.setExpiresIn(socialUser.getExpires_in());
            memberEntity.setUid(socialUser.getUid());
            return memberEntity;
        } else {
            //注册账号
            //db中没有社交关联账号，注册
            MemberEntity regist = new MemberEntity();
            regist.setAccessToken(socialUser.getAccess_token());
            regist.setExpiresIn(socialUser.getExpires_in());
            regist.setUid(socialUser.getUid());
            regist.setCreateTime(new Date());
            //try 处理网络波动
            try {
                //微博api获取当前微博账号绑定的用户信息
//            https://api.weibo.com/2/users/show.json
                HashMap<String, String> body = new HashMap<>();
                body.put("access_token", socialUser.getAccess_token());
                body.put("uid", socialUser.getUid());
                HttpResponse response = HttpUtils.doGet("https://api.weibo.com", "/2/users/show.json", "get", new HashMap<String, String>(), body);
                if (response.getStatusLine().getStatusCode() == 200) {
                    String json = EntityUtils.toString(response.getEntity());
                    JSONObject jsonObject = JSON.parseObject(json);
                    String name = jsonObject.getString("name");
                    String profile_image_url = jsonObject.getString("profile_image_url");
                    String gender = jsonObject.getString("gender");
                    regist.setNickname(name);
                    //女0 男1
                    regist.setGender("m".equals(gender) ? 0 : 1);
                    MemberLevelEntity levelEntity = memberLevelDao.selectLeveEntity(1);
                    regist.setLevelId(levelEntity.getId());//默认等级
                    regist.setHeader(profile_image_url);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            baseMapper.insert(regist);
            return regist;
        }
    }

    /**
     * 微信登录或者注册
     * @param access_token
     * @param openid
     * @return
     */

    @Override
    public MemberEntity socialLogin(String access_token, String openid) {

        //查询db中有社交关联账号
        MemberEntity memberEntity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("uid", openid));
        if (memberEntity != null) {
            //根据id更新数据 access_token和ExpiresIn
            MemberEntity update = new MemberEntity();
            update.setId(memberEntity.getId());
            update.setAccessToken(access_token);
            baseMapper.updateById(update);
            //返回最新entity
            memberEntity.setAccessToken(access_token);
            memberEntity.setUid(openid);
            return memberEntity;
        } else {
            //db中没有社交关联账号，注册
            MemberEntity regist = new MemberEntity();
            regist.setAccessToken(access_token);
            regist.setUid(openid);
            regist.setCreateTime(new Date());
            //try 处理网络波动
            try {
                //调用第三发方微博api
//            https://api.weibo.com/2/users/show.json
                HashMap<String, String> body = new HashMap<>();
                body.put("access_token", access_token);
                body.put("openid", openid);
                //https://api.weixin.qq.com/sns/userinfo?access_token=60_ugE6Y7WmaYMooet0Ag-REvpJNTgzK14ZM-7V5cVeMopS3sUklvEMvPXcaJ087yrjCx78y4Y5BsMR93SAMYgSbkilrYIcwX_-GB2fxBXAo3E&openid=o3_SC55dtAlrATP3_HCwonub5OOs
                HttpResponse response = HttpUtils.doGet("https://api.weixin.qq.com", "/sns/userinfo", "get", new HashMap<String, String>(), body);
                if (response.getStatusLine().getStatusCode() == 200) {
                    String json = EntityUtils.toString(response.getEntity());
                    JSONObject jsonObject = JSON.parseObject(json);
                    String nickname = jsonObject.getString("nickname");
                    String headimgurl = jsonObject.getString("headimgurl");
                    String gender = jsonObject.getString("sex");
                    regist.setNickname(nickname);
                    //女0 男1
                    regist.setGender(Integer.parseInt(gender));
                    MemberLevelEntity levelEntity = memberLevelDao.selectLeveEntity(1);
                    regist.setLevelId(levelEntity.getId());//默认等级
                    regist.setHeader(headimgurl);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            baseMapper.insert(regist);
            return regist;
        }
    }

    private void checkPhone(String phone) {
        QueryWrapper<MemberEntity> wrapper = new QueryWrapper<MemberEntity>().eq("mobile", phone);
        Integer count = this.baseMapper.selectCount(wrapper);
        if (count > 0) {
            throw new PhoneExistException();
        }
    }

    private void checkUserName(String username) {
        QueryWrapper<MemberEntity> wrapper = new QueryWrapper<MemberEntity>().eq("username", username);
        Integer count = this.baseMapper.selectCount(wrapper);
        if (count > 0) {
            throw new UserNameExistException();
        }
    }

}