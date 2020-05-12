package com.learn.miaosha.redis;

public class MiaoshaUserKey extends BasePrefix {
    public static final int TOKEN_EXPIRE = 3600*24*2;
    public MiaoshaUserKey(int expireSconds ,String prefix) {
        super(expireSconds,prefix);
    }
    public static MiaoshaUserKey token = new MiaoshaUserKey(TOKEN_EXPIRE,"token");
    public static MiaoshaUserKey getById = new MiaoshaUserKey(0,"id");

}
