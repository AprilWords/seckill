package com.learn.miaosha.redis;

public class AccesKey extends BasePrefix {

    public AccesKey(int expireSconds , String prefix)
    {
        super(expireSconds,prefix);
    }

    public static AccesKey access = new AccesKey(30000,"access");
    public static AccesKey withExpire(int expireSeconds){
        return new AccesKey(expireSeconds,"access");
    }

}
