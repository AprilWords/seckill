package com.learn.miaosha.rabbitmq;

import com.learn.miaosha.domain.MiaoshaOrder;
import com.learn.miaosha.domain.MiaoshaUser;
import com.learn.miaosha.service.GoodService;
import com.learn.miaosha.service.MiaoshaService;
import com.learn.miaosha.service.OrderService;
import com.learn.miaosha.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQReceiver {
    private static Logger log = LoggerFactory.getLogger(MQReceiver.class);


    @Autowired
    GoodService goodService;
    @Autowired
    OrderService orderService;
    @Autowired
    MiaoshaService miaoshaService;

    @RabbitListener(queues=MQConfig.QUEUE)
    public void receive(String message){
        log.info("receivemsg:"+message);

    }

    @RabbitListener(queues=MQConfig.TOPIC_QUEUE1)
    public void receiveTopic1(String message){
        log.info("topic queue1:"+message);

    }
    @RabbitListener(queues=MQConfig.TOPIC_QUEUE2)
    public void receiveTopic2( String message){
        log.info("topic queue2:"+message);

    }
    @RabbitListener(queues=MQConfig.HEADER_QUEUE)
    public void headerTopic2(byte[] message){
        log.info("header queue:"+new String(message));

    }


    @RabbitListener(queues=MQConfig.MIAOSHA_QUEUE)
    public void miaosha(String message){
        log.info("miaosha_queue:"+message);
        MiaoshaMessage mm = RabbitMQutil.stringToBean(message,MiaoshaMessage.class);
        long goodsId= mm.getGoodsId();
       MiaoshaUser miaoshaUser = mm.getMiaoshaUser();


         //判断库存
        GoodsVo goodsVo = goodService.getGoodsVoByGoodsId(goodsId);
                int stock = goodsVo.getStockCount();
                long userid = miaoshaUser.getId();
                long goodsid = goodsVo.getId();
                //判断库存是否为空
                if(stock<=0){

            return;
        }
        //判断是否已秒杀
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdGoods(userid,goodsid);
        if(miaoshaOrder!=null){

           return;
        }
         //1.减库存2.下订单3.写入秒杀订单
        miaoshaService.miaosha(miaoshaUser,goodsVo);


    }
}
