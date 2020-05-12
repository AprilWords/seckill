package com.learn.miaosha.access;

import com.learn.miaosha.domain.MiaoshaUser;

public class UserContext {
    //thread和当前线程绑定，放到当前线程   多线程状态下  不存在线程安全问题
    private static ThreadLocal<MiaoshaUser> userHolder =new ThreadLocal<MiaoshaUser>();
    public static void setUser(MiaoshaUser miaoshaUser){
        userHolder.set(miaoshaUser);
    }
    public static MiaoshaUser getUser(){
        return userHolder.get();
    }
}
