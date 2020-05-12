package com.learn.miaosha.service;

import com.learn.miaosha.domain.MiaoshaUser;
import com.learn.miaosha.result.CodeMsg;
import com.learn.miaosha.vo.LoginVo;

import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

public interface MiaoshaUserService {

    public MiaoshaUser getById(Long id);
    public boolean login(HttpServletResponse response,LoginVo loginVo);

    MiaoshaUser getByToken(HttpServletResponse response,String token);
    public boolean updatePassword(String token,Long id,String passwordNew);
}
