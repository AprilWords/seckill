package com.learn.miaosha.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Service
public class RedisService {
    @Autowired
    JedisPool jedisPool;

    @Autowired
    RedisConfig redisConfig;
    /*
    *获取单个对象
    *
    * */
    public <T> T get(KeyPrefix prefix,String key, Class<T> clazz) {

            Jedis jedis = null;
            try {
                jedis = jedisPool.getResource();
                //生成真正的key
                String realKey = prefix.getPrefix()+key;
                String str = jedis.get(realKey);
                T t = stringToBean(str, clazz);
                return t;
            }catch (RuntimeException e){
                e.printStackTrace();
            }
            finally {
            returnToPool(jedis);
        }
        return null;

    }
    /*
    * 删除
    * */
    public  boolean delete(KeyPrefix prefix,String key) {

        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            //生成真正的key
            String realKey = prefix.getPrefix()+key;
            Long ret = jedis.del(realKey);
            return ret>0;
        } finally {
            returnToPool(jedis);
        }

    }
    //判断key是否存在
    public  boolean isExist(KeyPrefix prefix,String key) {

        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            //生成真正的key
            String realKey = prefix.getPrefix()+key;
            boolean ret = jedis.exists(realKey);

            return ret;
        } finally {
            returnToPool(jedis);
        }

    }
    /*
    * 增加值
    * */
    public  Long incr(KeyPrefix prefix,String key) {

        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            //生成真正的key
            String realKey = prefix.getPrefix()+key;

            return jedis.incr(realKey);
        } finally {
            returnToPool(jedis);
        }

    }
    /*
    * 减少值
    *
    * */
    public  Long dec(KeyPrefix prefix,String key) {

        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            //生成真正的key
            String realKey = prefix.getPrefix()+key;
            ;

            return jedis.decr(realKey);
        } finally {
            returnToPool(jedis);
        }

    }
    /*
    *
    * 设置对象
    * */
    public <T> boolean set(KeyPrefix prefix,String key, T value) {

        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            //生成真正的key
            String realKey = prefix.getPrefix()+key;
            String st = beanToString(value);
            int seconds = prefix.expireSeconds();
            String ret;
            if(seconds<=0){
                ret = jedis.set(realKey,st);
            }else{
                ret = jedis.setex(realKey,seconds,st);
            }

           boolean result =  Boolean.getBoolean(ret);
           return result;
        } finally {
            returnToPool(jedis);
        }
    }

    private void returnToPool(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

    private <T> T stringToBean(String str, Class<T> clazz) {
        if (str == null || str.length() <= 0 || clazz == null) {
            return null;
        }
        if (clazz == int.class || clazz == Integer.class) {
            return (T) Integer.valueOf(str);

        } else if (clazz == String.class) {
            return (T) str;

        } else if (clazz == long.class || clazz == Long.class) {
            return (T) Long.valueOf(str);
        } else {
            return JSON.toJavaObject(JSON.parseObject(str), clazz);
        }


    }

    private <T> String beanToString(T value) {
        if (value == null) {
            return null;
        }
        Class<?> clazz = value.getClass();
        if (clazz == int.class || clazz == Integer.class) {
            return "" + value;

        } else if (clazz == String.class) {
            return (String) value;

        } else if (clazz == long.class || clazz == Long.class) {
            return "" + value;
        } else {
            return JSON.toJSONString(value);
        }


    }
}