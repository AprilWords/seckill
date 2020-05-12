package com.learn.miaosha.controller;

import com.learn.miaosha.domain.User;
import com.learn.miaosha.rabbitmq.MQReceiver;
import com.learn.miaosha.rabbitmq.MQSender;
import com.learn.miaosha.redis.RedisService;
import com.learn.miaosha.redis.UserKey;
import com.learn.miaosha.result.CodeMsg;
import com.learn.miaosha.result.Result;
import com.learn.miaosha.service.UserSrevice;
import com.sun.corba.se.spi.orbutil.fsm.Guard;
import com.sun.net.httpserver.Authenticator;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/demo")
public class SampleController {
    @Autowired
    UserSrevice userSrevice;

    @Autowired
    RedisService redisService;
    @Autowired
    MQSender mqSender;
    @Autowired
    MQReceiver mqReceiver;

    @RequestMapping("/thymeleaf")
    public String thymeleaf(Model model){
        model.addAttribute("name","Joshua");
        return  "hello";
    }

    @RequestMapping("/mq")
    @ResponseBody
    public Result<String> mq() {
      mqSender.send("helloworld");
        return Result.success("hello,world");
    }

    @RequestMapping("/mq/topic")
    @ResponseBody
    public Result<String> mqtopic() {
        mqSender.send("helloworld");
        return Result.success("hello,world");
    }

    @RequestMapping("/mq/fanout")
    @ResponseBody
    public Result<String> mqFanout() {
        mqSender.sendFanout("helloworld");
        return Result.success("hello,world");
    }

    @RequestMapping("/mq/header")
    @ResponseBody
    public Result<String> mqHeader() {
        mqSender.sendHeaders("helloworld");
        return Result.success("hello,world");
    }



    @RequestMapping("/helloerror")
    @ResponseBody
    public Result<String> helloError() {
        //User user = userSrevice.getById(1);

        // return Result.success()
        return Result.error(CodeMsg.SERVER_ERROR);
    }
    @RequestMapping("/redis/get")
    @ResponseBody
    public Result<User> redisGet(){

        User user = redisService.get(UserKey.getById,""+1,User.class);
        // return Result.success()
        return Result.success(user);

    }
    @RequestMapping("/redis/set")
    @ResponseBody
    public Result<Boolean> redisSet(){


        User user =new User();
        user.setId(1);
        user.setName("111111");
        boolean ret = redisService.set(UserKey.getById,""+1,user);
       //String v1 = redisService.get("2",String.class);
        // return Result.success()
        return Result.success(true);

    }

}
