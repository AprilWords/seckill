package com.learn.miaosha.controller;

import com.learn.miaosha.redis.RedisService;
import com.learn.miaosha.result.CodeMsg;
import com.learn.miaosha.result.Result;
import com.learn.miaosha.service.MiaoshaUserService;
import com.learn.miaosha.service.UserSrevice;
import com.learn.miaosha.util.ValidatorUtil;
import com.learn.miaosha.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static com.learn.miaosha.result.Result.success;

@Controller
@RequestMapping("/login")
public class LoginController {
    private static Logger log = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    MiaoshaUserService miaoshaUserService;

    @Autowired
    RedisService redisService;
    @RequestMapping("/to_login")
    public String toLogin(){
        return  "login";
    }
    @RequestMapping("/do_login")
    @ResponseBody
    public Result<Boolean> doLogin(HttpServletResponse response, @Valid LoginVo loginVo){
        log.info(loginVo.toString());
        //登陆
      miaoshaUserService.login(response,loginVo);
      return Result.success(true);
    }


}

