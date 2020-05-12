package com.learn.miaosha.redis;

public abstract class  BasePrefix implements KeyPrefix {
    private int expireSeconds;
    private String prefix;
    public BasePrefix(int expireSeconds,String prefix){
        this.expireSeconds= expireSeconds;
        this.prefix=prefix;
    }
    public BasePrefix(String prefix) {//0代表永不过期
       this(0, prefix);
    }
    @Override
    public int expireSeconds() {//默认0代表永不过期
        return expireSeconds;
    }

    @Override
    public String getPrefix() {
        String getClassName = getClass().getSimpleName();
        return getClassName+":"+prefix;
    }
}
