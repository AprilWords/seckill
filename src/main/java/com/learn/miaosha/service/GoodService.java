package com.learn.miaosha.service;

import com.learn.miaosha.dao.GoodsDao;
import com.learn.miaosha.domain.Goods;
import com.learn.miaosha.domain.MiaoshaGoods;
import com.learn.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public  class GoodService {
    @Autowired
    GoodsDao goodsDao;
    public List<GoodsVo> listGoodsVo(){
        return goodsDao.listGoodsVo();
    }
    public GoodsVo getGoodsVoByGoodsId(long goodsId){
        return goodsDao.getGoodsVoByGoodsId(goodsId);
    }

    public boolean reduceStock(GoodsVo goodsVo) {
        MiaoshaGoods miaoshaGoods = new MiaoshaGoods();
        miaoshaGoods.setGoodsId(goodsVo.getId());
        miaoshaGoods.setGoodsId(goodsVo.getId());
        int ret = goodsDao.reduceStock(miaoshaGoods);
        return  ret>0;
    }
}
