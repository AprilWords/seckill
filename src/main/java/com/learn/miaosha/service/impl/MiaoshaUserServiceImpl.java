package com.learn.miaosha.service.impl;

import com.learn.miaosha.dao.MiaoshaUserDao;
import com.learn.miaosha.domain.MiaoshaUser;
import com.learn.miaosha.exception.GlobleException;
import com.learn.miaosha.redis.MiaoshaUserKey;
import com.learn.miaosha.redis.RedisService;
import com.learn.miaosha.result.CodeMsg;
import com.learn.miaosha.service.MiaoshaUserService;
import com.learn.miaosha.util.MD5Util;
import com.learn.miaosha.util.UUIDUtil;
import com.learn.miaosha.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.CoderMalfunctionError;

@Service
public class MiaoshaUserServiceImpl implements MiaoshaUserService {
    @Autowired
    MiaoshaUserDao miaoshaUserDao;

    @Autowired
    RedisService redisService;
    public static final String COOKIE_NAME_TOKEN = "token";

    @Override
    public MiaoshaUser getById(Long id) {
        //取缓存
        MiaoshaUser miaoshaUser = redisService.get(MiaoshaUserKey.getById,""+id,MiaoshaUser.class);
        if(miaoshaUser!=null){
            return miaoshaUser;
        }
        //取数据库数据
        miaoshaUser = miaoshaUserDao.getById(id);
        //若取到数据放入redis中
        if(miaoshaUser!=null){
            redisService.set(MiaoshaUserKey.getById,""+id,miaoshaUser);
        }

        return miaoshaUser;
    }
    @Override
    public boolean updatePassword(String token,Long id,String formPass) {
        //取user
        MiaoshaUser user = getById(id);
        if(user == null){
            throw new GlobleException(CodeMsg.MOBILE_NOT_EXIST);
        }
        MiaoshaUser toBeUpdate = new MiaoshaUser();
        toBeUpdate.setId(id);
        toBeUpdate.setPassword(MD5Util.formPassDBPass(formPass,user.getSalt()));
        miaoshaUserDao.update(toBeUpdate);
        //处理缓存
        redisService.delete(MiaoshaUserKey.getById,""+id);
        user.setPassword(toBeUpdate.getPassword());
        redisService.set(MiaoshaUserKey.token,token,user);
        return true;


    }

    @Override
    public boolean login(HttpServletResponse response,LoginVo loginVo) {
        if(loginVo ==null){
            throw new GlobleException(CodeMsg.MOBILE_EMPTY);
        }
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        //判断手机号是否存在
        MiaoshaUser user = miaoshaUserDao.getById(Long.parseLong((mobile)));
        if(user ==null){
            throw new GlobleException(CodeMsg.MOBILE_EMPTY);
        }
        //验证密码
        String dbPass = user.getPassword();
        String dbSalt =  user.getSalt();
        String calcPass = MD5Util.formPassDBPass(password,dbSalt);
        if(!calcPass.equals(dbPass)){
            throw new GlobleException(CodeMsg.PASSWORD_ERROR);
        }
        String token = UUIDUtil.uuid();
        //生成cookie
        addCookie(response,user,token);
        return true;
    }



    @Override
    public MiaoshaUser getByToken(HttpServletResponse response,String token) {
        if(StringUtils.isEmpty(token)){
            return null;
        }
        MiaoshaUser user = redisService.get(MiaoshaUserKey.token,token,MiaoshaUser.class);
        //延长有效期
        if(user!=null){
            addCookie(response,user,token);
        }

        return user;
    }
    private void addCookie(HttpServletResponse response,MiaoshaUser user,String token){

        redisService.set(MiaoshaUserKey.token,token,user);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN,token);
        cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }

}
