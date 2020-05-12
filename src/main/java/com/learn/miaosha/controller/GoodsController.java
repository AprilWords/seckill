package com.learn.miaosha.controller;

import com.learn.miaosha.domain.MiaoshaUser;
import com.learn.miaosha.redis.GoodsKey;
import com.learn.miaosha.redis.RedisService;
import com.learn.miaosha.result.Result;
import com.learn.miaosha.service.GoodService;
import com.learn.miaosha.service.MiaoshaUserService;
import com.learn.miaosha.service.impl.MiaoshaUserServiceImpl;
import com.learn.miaosha.vo.GoodsDetailVo;
import com.learn.miaosha.vo.GoodsVo;
import com.learn.miaosha.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("login/goods")
public class GoodsController {
    @Autowired
    MiaoshaUserService miaoshaUserService;
    @Autowired
    GoodService goodService;
    @Autowired
    RedisService redisService;
    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;
    @Autowired
    ApplicationContext applicationContext;
    @RequestMapping(value = "/to_list",produces = "text/html")
    @ResponseBody
    public String toLogin(HttpServletResponse response, HttpServletRequest request,Model model, MiaoshaUser user){
    model.addAttribute("user",user);
    //查询商品列表
        //从缓存中取

        List<GoodsVo> goodList = goodService.listGoodsVo();
        model.addAttribute("goodsList",goodList);
        String html =redisService.get(GoodsKey.getGoodsList,"",String.class);
        if(!StringUtils.isEmpty(html)){
            return html;
        }
        //手动渲染
        SpringWebContext ctx = new SpringWebContext(request,response,request.getServletContext(),request.getLocale(),model.asMap(),applicationContext);

        html =   thymeleafViewResolver.getTemplateEngine().process("goods_list",ctx);
        if(!StringUtils.isEmpty(html)){
            redisService.set(GoodsKey.getGoodsList,"",html);
        }
        return html;


    }
    @RequestMapping(value = "/to_detail2/{goodsId}",produces ="text/html")
    @ResponseBody
    public String detail2Model (HttpServletResponse response, HttpServletRequest request,Model model, MiaoshaUser miaoshaUser,
                               @PathVariable("goodsId")long goodsId){
     /*  long goodsID = Long.valueOf(goodsId);*/
        //从缓存中取
        String html =redisService.get(GoodsKey.getGoodsDetail,""+goodsId,String.class);
        if(!StringUtils.isEmpty(html)){
            return html;
        }
        GoodsVo goodsVo = goodService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods",goodsVo);
        model.addAttribute("user",miaoshaUser);

        //手动渲染
        long startAt = goodsVo.getStartDate().getTime();
        long endAt = goodsVo.getEndDate().getTime();
        long nowAt = System.currentTimeMillis();
        int miaoshaStatus =0;
        int remainSeconds = 0;
        if(nowAt<startAt){//秒杀未开始
           miaoshaStatus =0;
            remainSeconds=(int)((startAt-nowAt)/1000);
        }else if(nowAt>endAt){//秒杀已结束
            miaoshaStatus =2;
            remainSeconds=-1;
        }
        else {//秒杀进行中
            miaoshaStatus =1;
            remainSeconds=0;
        }
        model.addAttribute("maiaoshaStatus",miaoshaStatus);
        model.addAttribute("remainSeconds",remainSeconds);
       /* return "goods_detail";*/
        //手动渲染
        SpringWebContext ctx = new SpringWebContext(request,response,request.getServletContext(),request.getLocale(),model.asMap(),applicationContext);
        html =   thymeleafViewResolver.getTemplateEngine().process("goods_detail",ctx);
        if(!StringUtils.isEmpty(html)){
            redisService.set(GoodsKey.getGoodsDetail,""+goodsId,html);
        }
        return html;


    }
    @RequestMapping(value = "/to_detail/{goodsId}",produces ="application/json")
    @ResponseBody
    public Result<GoodsDetailVo> detailModel (HttpServletResponse response, HttpServletRequest request, Model model, MiaoshaUser miaoshaUser,
                                               @PathVariable("goodsId")long goodsId){

        GoodsVo goodsVo = goodService.getGoodsVoByGoodsId(goodsId);


        long startAt = goodsVo.getStartDate().getTime();
        long endAt = goodsVo.getEndDate().getTime();
        long nowAt = System.currentTimeMillis();
        int miaoshaStatus =0;
        int remainSeconds = 0;
        if(nowAt<startAt){
            //秒杀未开始
            miaoshaStatus =0;
            remainSeconds=(int)((startAt-nowAt)/1000);
        }else if(nowAt>endAt){//秒杀已结束
            miaoshaStatus =2;
            remainSeconds=-1;
        }
        else {//秒杀进行中
            miaoshaStatus =1;
            remainSeconds=0;
        }
        model.addAttribute("maiaoshaStatus",miaoshaStatus);
        model.addAttribute("remainSeconds",remainSeconds);
        GoodsDetailVo vo = new GoodsDetailVo();
        vo.setGoodsVo(goodsVo);
        vo.setMiaoshaUser(miaoshaUser);
        vo.setMiaoshaStatus(miaoshaStatus);
        vo.setRemainSeconds(remainSeconds);

         return Result.success(vo);


    }


}
