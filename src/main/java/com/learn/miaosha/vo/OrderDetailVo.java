package com.learn.miaosha.vo;

import com.learn.miaosha.domain.Goods;
import com.learn.miaosha.domain.OrderInfo;


public class OrderDetailVo {
    private GoodsVo goodsVo;

    public GoodsVo getGoodsVo() {
        return goodsVo;
    }

    public void setGoodsVo(GoodsVo goodsVo) {
        this.goodsVo = goodsVo;
    }

    public OrderInfo getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(OrderInfo orderInfo) {
        this.orderInfo = orderInfo;
    }

    private OrderInfo orderInfo;

}
