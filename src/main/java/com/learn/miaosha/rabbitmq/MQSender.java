package com.learn.miaosha.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service

public class MQSender {
    private static Logger log = LoggerFactory.getLogger(MQSender.class);

    @Autowired
    AmqpTemplate amqpTemplate;
    public void send(Object message){
        String msg =RabbitMQutil.beanToString(message);
        log.info("sendmsg:"+msg);
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE,"topic.key1",msg+"1");
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE,"topic.key2",msg+"2");
    }



    public void sendFanout(Object message){
        String msg =RabbitMQutil.beanToString(message);
        log.info("sendmsg:"+msg);
        amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE,"",msg);

    }
    public void sendHeaders(Object message){
        String msg =RabbitMQutil.beanToString(message);
        log.info("sendmsg:"+msg);
        MessageProperties properties = new MessageProperties();
        properties.setHeader("header1","value1");
        properties.setHeader("header2","value2");
        Message obj = new Message(msg.getBytes(),properties);
        amqpTemplate.convertAndSend(MQConfig.HEADERS_EXCHANGE,MQConfig.HEADER_QUEUE,obj);

    }

    public void sendMiaoshaMessage(MiaoshaMessage mm) {



        String msg =RabbitMQutil.beanToString(mm);
        log.info("sendmsg:"+msg);
        amqpTemplate.convertAndSend(MQConfig.MIAOSHA_QUEUE,msg);

    }
    }