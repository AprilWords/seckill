package com.learn.miaosha.controller;

import com.learn.miaosha.domain.MiaoshaUser;
import com.learn.miaosha.redis.RedisService;
import com.learn.miaosha.result.Result;
import com.learn.miaosha.service.GoodService;
import com.learn.miaosha.service.MiaoshaUserService;
import com.learn.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.session.SessionProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    MiaoshaUserService miaoshaUserService;
    @Autowired
    RedisService redisService;
    @RequestMapping("/info")
    @ResponseBody
    public Result<MiaoshaUser> getUserInfo(Model model, MiaoshaUser user){
    model.addAttribute("user",user);
        return Result.success(user);


    }

}
