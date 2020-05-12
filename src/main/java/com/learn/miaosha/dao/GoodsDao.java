package com.learn.miaosha.dao;

import com.learn.miaosha.domain.MiaoshaGoods;
import com.learn.miaosha.domain.MiaoshaUser;
import com.learn.miaosha.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;


@Mapper
public interface GoodsDao {
    @Select("SELECT g.*,mg.stock_count,mg.start_date,mg.end_date,mg.miaosha_price FROM miaosha_goods mg LEFT JOIN goods g ON mg.id= g.id")
    public List<GoodsVo> listGoodsVo();
    @Select("SELECT g.*,mg.stock_count,mg.start_date,mg.end_date,mg.miaosha_price FROM miaosha_goods mg LEFT JOIN goods g ON mg.id= g.id where g.id= #{goodsId}")
    public GoodsVo getGoodsVoByGoodsId(@Param("goodsId") long goodsId);
    @Update("update miaosha_goods set stock_count = stock_count-1 where goods_id = #{goodsId} and stock_count>0")
    public int reduceStock(MiaoshaGoods goods);
}
