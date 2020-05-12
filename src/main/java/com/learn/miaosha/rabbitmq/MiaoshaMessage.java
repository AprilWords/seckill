package com.learn.miaosha.rabbitmq;

import com.learn.miaosha.domain.MiaoshaUser;

public class MiaoshaMessage {
    public MiaoshaUser getMiaoshaUser() {
        return miaoshaUser;
    }

    public void setMiaoshaUser(MiaoshaUser miaoshaUser) {
        this.miaoshaUser = miaoshaUser;
    }

    public long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }

    private MiaoshaUser miaoshaUser;
    private long goodsId;
}
