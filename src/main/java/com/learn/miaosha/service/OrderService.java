package com.learn.miaosha.service;

import com.learn.miaosha.dao.GoodsDao;
import com.learn.miaosha.dao.OrderDao;
import com.learn.miaosha.domain.MiaoshaOrder;
import com.learn.miaosha.domain.MiaoshaUser;
import com.learn.miaosha.domain.OrderInfo;
import com.learn.miaosha.redis.OrderKey;
import com.learn.miaosha.redis.RedisService;
import com.learn.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.session.SessionProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public  class OrderService {
    @Autowired
    OrderDao orderDao;
    @Autowired
    RedisService redisService;


    public MiaoshaOrder getMiaoshaOrderByUserIdGoods(long userid, long goodsid) {


       /* orderDao.getMiaoshaOrderByUserIdGoods(userid,goodsid);*/
        MiaoshaOrder miaoshaOrder = redisService.get(OrderKey.getMiaoshaOrderByUidGid,""+userid+"_"+goodsid,MiaoshaOrder.class);
        return miaoshaOrder;
    }
    @Transactional
    public OrderInfo CreateOrder(MiaoshaUser miaoshaUser, GoodsVo goodsVo) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsPrice(goodsVo.getMiaoshaPrice());
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goodsVo.getId());
        orderInfo.setGoodsName(goodsVo.getGoodsName());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(miaoshaUser.getId());
         orderDao.insert(orderInfo);
        MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
        miaoshaOrder.setGoodsId(goodsVo.getId());
        miaoshaOrder.setOrderId(orderInfo.getId());
        miaoshaOrder.setUserId(miaoshaUser.getId());
        orderDao.insertMiaoshaOrder(miaoshaOrder);


        redisService.set(OrderKey.getMiaoshaOrderByUidGid,""+miaoshaUser.getId()+"_"+goodsVo.getId(),miaoshaOrder);
        return orderInfo;
    }
    public OrderInfo getOrderById(long orderId){

        return orderDao.getOrderByID(orderId);
    }
}
