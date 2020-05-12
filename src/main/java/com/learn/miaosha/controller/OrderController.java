package com.learn.miaosha.controller;

import com.learn.miaosha.domain.MiaoshaUser;
import com.learn.miaosha.domain.OrderInfo;
import com.learn.miaosha.redis.GoodsKey;
import com.learn.miaosha.redis.RedisService;
import com.learn.miaosha.result.CodeMsg;
import com.learn.miaosha.result.Result;
import com.learn.miaosha.service.GoodService;
import com.learn.miaosha.service.MiaoshaUserService;
import com.learn.miaosha.service.OrderService;
import com.learn.miaosha.vo.GoodsDetailVo;
import com.learn.miaosha.vo.GoodsVo;
import com.learn.miaosha.vo.OrderDetailVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    MiaoshaUserService miaoshaUserService;
    @Autowired
   RedisService redisService;
    @Autowired
    OrderService orderService;
    @Autowired
    GoodService goodService;


    @RequestMapping("/detail")
    @ResponseBody
    public Result<OrderDetailVo> info(Model model, MiaoshaUser miaoshaUser, @RequestParam("orderId") long orderId){
        if(miaoshaUser == null){
            return Result.error(CodeMsg.SESSION_EMPTY);
        }
        OrderInfo orderInfo = orderService.getOrderById(orderId);
        if(orderInfo==null){
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }
      long goodsId = orderInfo.getGoodsId();
        GoodsVo goodsVo = goodService.getGoodsVoByGoodsId(goodsId);
        OrderDetailVo orderDetailVo = new OrderDetailVo();
        orderDetailVo.setGoodsVo(goodsVo);
        orderDetailVo.setOrderInfo(orderInfo);
        return Result.success(orderDetailVo);
    }

}
