package com.learn.miaosha.redis;

public class UserKey extends BasePrefix {
    private UserKey(String prefix) {
        super(prefix);
    }
    public UserKey(int expireSeconds,String prefix){
        super(expireSeconds, prefix);
    }
    public static UserKey getById = new UserKey("id");
    public static UserKey getByName = new UserKey("Name");
}