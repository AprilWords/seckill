package com.learn.miaosha.service;

import com.learn.miaosha.dao.GoodsDao;
import com.learn.miaosha.domain.Goods;
import com.learn.miaosha.domain.MiaoshaOrder;
import com.learn.miaosha.domain.MiaoshaUser;
import com.learn.miaosha.domain.OrderInfo;
import com.learn.miaosha.redis.MiaoshaKey;
import com.learn.miaosha.redis.RedisService;
import com.learn.miaosha.util.MD5Util;
import com.learn.miaosha.util.UUIDUtil;
import com.learn.miaosha.vo.GoodsVo;
import com.learn.miaosha.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

@Service
public class MiaoshaService {
    @Autowired
    GoodService goodService;
    @Autowired
    OrderService orderService;
    @Autowired
    RedisService redisService;


    @Transactional
    public OrderInfo miaosha(MiaoshaUser miaoshaUser, GoodsVo goodsVo){
        //减缓存 下订单 写入秒杀订单
       boolean success =  goodService.reduceStock(goodsVo);
       if(success) {
           //生成订单
           return   orderService.CreateOrder(miaoshaUser, goodsVo);
       }else {
           setGoodsOver(goodsVo.getId());
           return null;
       }




    }

    private void setGoodsOver(Long id) {
        redisService.set(MiaoshaKey.isGoodsOVer,""+id,true);
    }

    public long getMiaoshaResult(Long id, long goodsId) {
       MiaoshaOrder miaoshaOrder =  orderService.getMiaoshaOrderByUserIdGoods(id,goodsId);
       if(miaoshaOrder!=null){
           return miaoshaOrder.getOrderId();
       }else{
           boolean  isOVero = getGoodsOver(goodsId);
           if(isOVero){
               return -1;
           }else{
               return 0;
           }
       }
    }

    private boolean getGoodsOver(long goodsId) {
        return redisService.isExist(MiaoshaKey.isGoodsOVer, "" + goodsId);
    }

    public boolean checkPath(MiaoshaUser miaoshaUser, long goodsId, String path) {
      if(miaoshaUser==null||path==null){
          return false;
      }
       String pathOld =  redisService.get(MiaoshaKey.getMiaoshaPaht,""+miaoshaUser.getId()+"_"+goodsId,String.class);
       return path.equals(pathOld);
    }

    public String createMiaoshaPath(MiaoshaUser miaoshaUser, long goodsId) {
        String str = MD5Util.md5(UUIDUtil.uuid()+"123456");
        redisService.set(MiaoshaKey.getMiaoshaPaht,""+miaoshaUser.getId()+"_"+goodsId,str);
        return str;
    }

    public BufferedImage createMiaoshaVerifyCode(MiaoshaUser miaoshaUser, long goodsId) {
        if(miaoshaUser==null||goodsId<=0){
            return null;
        }
       int width = 80;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        //set the background color
        g.setColor(new Color(0xDCDCDC));
        g.drawRect(0,0,width-1,height-1);
        //draw the border
        g.setColor(Color.BLACK);
        g.drawRect(0,0,width-1,height-1);
        //create a random instance generate the codes
        Random rdm = new Random();
        //make some confusion
        for(int i =0;i<50;i++){
            int x =rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x,y,0,0);
        }
        //generate a random code
        String verifyCode = generateMiaoshaVerifyCode(rdm);
        g.setColor(new Color(0,100,0));
        g.setFont(new Font("Candara",Font.BOLD,24));
        g.drawString(verifyCode,8,24);
        g.dispose();
        //save the verycode to redis
        int rmd = calc(verifyCode);
        redisService.set(MiaoshaKey.VERIFY_CODE,miaoshaUser.getId()+","+goodsId,rmd);

        return image;
    }
   /* public static void main(String[]args){
        System.out.println(calc("1+3+5"));
    }*/
    private static int calc(String exp) {
        try{
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return (Integer) engine.eval(exp);


        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    private static char[] ops = new char[]{'+','-','*','/'};

    private String generateMiaoshaVerifyCode(Random rdm) {
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        char op1= ops[rdm.nextInt(3)];
        char op2 =ops[rdm.nextInt(3)];
        String exp = ""+num1+op1+num2+op2+num3;
        return exp;

    }

    public boolean checkVerifyCode(MiaoshaUser miaoshaUser, long goodsId, int verifyCode) {
        if(miaoshaUser==null||goodsId<=0){
            return false;
        }
        Integer codeOld = redisService.get(MiaoshaKey.VERIFY_CODE,miaoshaUser.getId()+","+goodsId,Integer.class);
        if(codeOld==null||codeOld-verifyCode!=0){
            return false;
        }
        redisService.delete(MiaoshaKey.VERIFY_CODE,miaoshaUser.getId()+","+goodsId);
        return true;

    }
}
