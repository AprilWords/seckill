package com.learn.miaosha.redis;

public class MiaoshaKey extends BasePrefix {
    private MiaoshaKey(int expireSeconds,String prefix) {
        super(expireSeconds,prefix);
    }


    public static MiaoshaKey isGoodsOVer = new MiaoshaKey(0,"go");
    public static MiaoshaKey getMiaoshaPaht = new MiaoshaKey(60,"mp");
    public static MiaoshaKey VERIFY_CODE = new MiaoshaKey(300,"vc");

}
