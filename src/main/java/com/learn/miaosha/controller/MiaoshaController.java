package com.learn.miaosha.controller;

import com.learn.miaosha.access.AccessLimit;
import com.learn.miaosha.domain.MiaoshaOrder;
import com.learn.miaosha.domain.MiaoshaUser;
import com.learn.miaosha.domain.OrderInfo;
import com.learn.miaosha.rabbitmq.MQSender;
import com.learn.miaosha.rabbitmq.MiaoshaMessage;
import com.learn.miaosha.redis.AccesKey;
import com.learn.miaosha.redis.GoodsKey;
import com.learn.miaosha.redis.MiaoshaKey;
import com.learn.miaosha.redis.RedisService;
import com.learn.miaosha.result.CodeMsg;
import com.learn.miaosha.result.Result;
import com.learn.miaosha.service.GoodService;
import com.learn.miaosha.service.MiaoshaService;
import com.learn.miaosha.service.MiaoshaUserService;
import com.learn.miaosha.service.OrderService;
import com.learn.miaosha.util.MD5Util;
import com.learn.miaosha.util.UUIDUtil;
import com.learn.miaosha.vo.GoodsVo;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {
    @Autowired
    MiaoshaUserService miaoshaUserService;
    @Autowired
    GoodService goodService;
    @Autowired
    OrderService orderService;
    @Autowired
    MiaoshaService miaoshaService;
    @Autowired
    RedisService redisService;
    @Autowired
    MQSender mqSender;
    private Map<Long,Boolean> localOverMap = new HashMap<>();
    @RequestMapping(value = "/{path}/do_miaosha",method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> list(Model model, MiaoshaUser miaoshaUser,
                                @RequestParam("goodsId")long goodsId,
                                @PathVariable("path")String path){
        model.addAttribute("user",miaoshaUser);
        if(miaoshaUser ==null){
            return Result.error(CodeMsg.SESSION_EMPTY);
        }
        //验证path
        boolean check = miaoshaService.checkPath(miaoshaUser,goodsId,path);
        if(!check){
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }



        boolean over= localOverMap.get(goodsId);
        //利用内存标记减少redis压力
        if(over){
            return Result.error(CodeMsg.MIAOSHA_OVER);
        }

        //预减redis中的缓存
       long stock = redisService.dec(GoodsKey.getMiaoshaoGoodsStock,""+goodsId);
        if(stock<0){
            localOverMap.put(goodsId,true);
            return Result.error(CodeMsg.MIAOSHA_OVER);
        }
        //判断是否已秒杀
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdGoods(miaoshaUser.getId(),goodsId);
        if(miaoshaOrder!=null){

            return Result.error(CodeMsg.REPEATIVE_MIAOSHA);
        }
        //入队
        MiaoshaMessage mm= new MiaoshaMessage();
        mm.setGoodsId(goodsId);
        mm.setMiaoshaUser(miaoshaUser);
        mqSender.sendMiaoshaMessage(mm);

        return Result.success(0);//排队中



       /* //判断库存
        GoodsVo goodsVo = goodService.getGoodsVoByGoodsId(goodsId);
        int stock = goodsVo.getStockCount();
        long userid = miaoshaUser.getId();
        long goodsid = goodsVo.getId();
        //判断库存是否为空
        if(stock<=0){
            model.addAttribute("errormsg", CodeMsg.MIAOSHA_OVER.getMsg());
            return Result.error(CodeMsg.MIAOSHA_OVER);
        }
        //判断是否已秒杀
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdGoods(userid,goodsid);
        if(miaoshaOrder!=null){

            model.addAttribute("errormsg", CodeMsg.REPEATIVE_MIAOSHA.getMsg());
            return Result.error(CodeMsg.REPEATIVE_MIAOSHA);
        }
        //1.减库存2.下订单3.写入秒杀订单
        OrderInfo orderInfo = miaoshaService.miaosha(miaoshaUser,goodsVo);
        model.addAttribute("orderInfo",orderInfo);
        model.addAttribute("goods",goodsVo);
        return Result.success(orderInfo);*/
       /* return Result.success(orderInfo);*/
    }
    /*
    * orderid成功
    * 1 秒杀失败
    * 0 排队中
    *
    *
    * */
    @RequestMapping(value = "/result",method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> miaoshaResult(Model model, MiaoshaUser miaoshaUser, @RequestParam("goodsId")long goodsId){
        if(miaoshaUser==null){
            return Result.error(CodeMsg.SERVER_ERROR);
        }
      long result =  miaoshaService.getMiaoshaResult(miaoshaUser.getId(),goodsId);
        return Result.success(result);
    }

    @AccessLimit(seconds=1800,maxCount=5,needLogin=true)
    @RequestMapping(value = "/path",method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaPath(Model model, MiaoshaUser miaoshaUser,
                                         @RequestParam("goodsId")long goodsId,
                                         @RequestParam(value="verifyCode",defaultValue = "0")int verifyCode,
                                         HttpServletRequest requeset){
        if(miaoshaUser==null){
            return Result.error(CodeMsg.SERVER_ERROR);
        }
       /* //查询访问次数
        String uri = requeset.getRequestURI();//访问路径
        String key = uri+"_"+miaoshaUser.getId()+"_"+goodsId;
        Integer count = redisService.get(AccesKey.access,key,Integer.class);
        if(count==null){
            redisService.set(AccesKey.access,key,1);
        }else if(count<5){
            redisService.incr(AccesKey.access,key);//不足五次自加1
        }else{
            return Result.error(CodeMsg.REQUEST_LIMIT);
        }*/

        //check verifycode
        boolean check = miaoshaService.checkVerifyCode(miaoshaUser,goodsId,verifyCode);
        if(!check){
            return Result.error(CodeMsg.REQUEST_ILLEGAL);

        }
        String path = miaoshaService.createMiaoshaPath(miaoshaUser,goodsId);

        return Result.success(path);
    }
    @RequestMapping(value = "/verifyCode",method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaverifyCode(HttpServletResponse response,
                                               Model model, MiaoshaUser miaoshaUser, @RequestParam("goodsId")long goodsId) throws IOException {
        if(miaoshaUser==null){
            return Result.error(CodeMsg.SERVER_ERROR);
        }
        BufferedImage image = miaoshaService.createMiaoshaVerifyCode(miaoshaUser,goodsId);
        OutputStream out=null;
    try{
        out = response.getOutputStream();
        ImageIO.write(image,"JPEG",out);

        return null;

    }catch (IOException e){
        e.printStackTrace();
        return  Result.error(CodeMsg.MIAOSHA_OVER);
    }finally {
        out.flush();
        out.close();
    }

    }

    //系统初始化
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVoList = goodService.listGoodsVo();
        if (goodsVoList == null) {
            return;
        }
        for(GoodsVo goods:goodsVoList){
            redisService.set(GoodsKey.getMiaoshaoGoodsStock,""+goods.getId(),goods.getStockCount());
            localOverMap.put(goods.getId(),false);
        }
    }
}
