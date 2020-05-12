package com.learn.miaosha.vo;

import com.learn.miaosha.domain.Goods;
import com.learn.miaosha.domain.MiaoshaUser;

import java.util.Date;

public class GoodsDetailVo extends Goods {
    public int getMiaoshaStatus() {
        return miaoshaStatus;
    }

    public void setMiaoshaStatus(int miaoshaStatus) {
        this.miaoshaStatus = miaoshaStatus;
    }

    public int getRemainSeconds() {
        return remainSeconds;
    }

    public void setRemainSeconds(int remainSeconds) {
        this.remainSeconds = remainSeconds;
    }

    public GoodsVo getGoodsVo() {
        return goodsVo;
    }

    public void setGoodsVo(GoodsVo goodsVo) {
        this.goodsVo = goodsVo;
    }

    private int miaoshaStatus =0;
    private int remainSeconds = 0;
    private GoodsVo goodsVo;

    public MiaoshaUser getMiaoshaUser() {
        return miaoshaUser;
    }

    public void setMiaoshaUser(MiaoshaUser miaoshaUser) {
        this.miaoshaUser = miaoshaUser;
    }

    private MiaoshaUser miaoshaUser;

}
