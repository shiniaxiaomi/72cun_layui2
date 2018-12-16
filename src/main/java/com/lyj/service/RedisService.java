package com.lyj.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.lyj.redisKey.KeyPrefix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;
import java.util.Map;

/**
 * Created by 陆英杰
 * 2018/12/16 21:28
 */

@Service
public class RedisService {



    @Autowired
    JedisPool jedisPool;

    //获取一个对象
    public <T> T get(KeyPrefix keyPrefix, Object key, Class<T> clazz){
        Jedis jedis=null;
        try{
            jedis = jedisPool.getResource();
            //生成真正的key
            String realKey=keyPrefix.getPrefix()+String.valueOf(key);

            String str=jedis.get(realKey);
            T t=stringToBean(str,clazz);//吧一个字符串转化成一个bean对象
            return t;
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
    }

    //获取一个List
    public <T> List<T> getList(KeyPrefix keyPrefix, Object key, Class<T> clazz){
        Jedis jedis=null;
        try{
            jedis = jedisPool.getResource();
            //生成真正的key
            String realKey=keyPrefix.getPrefix()+String.valueOf(key);

            String str=jedis.get(realKey);
            return stringToList(str,clazz);//吧一个字符串转化成一个bean对象
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
    }

    //保存一个对象
    public <T> boolean set(KeyPrefix keyPrefix,Object key,T value){
        Jedis jedis=null;
        try{
            jedis = jedisPool.getResource();

            String str=beanToString(value);//吧一个bean对象转化成一个字符串
            if(str==null || str.length()<=0){
                return false;
            }

            //生成真正的key
            String realKey=keyPrefix.getPrefix()+String.valueOf(key);
            int expireSecond = keyPrefix.expireSecond();
            if(expireSecond<=0){//永不过期
                jedis.set(realKey, str);
            }else{
                jedis.setex(realKey,expireSecond,str);//设置过期时间
            }


            return true;
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }

    }

    //判断是否存在
    public <T> boolean exists(KeyPrefix keyPrefix,Object key){
        Jedis jedis=null;
        try{
            jedis = jedisPool.getResource();
            //生成真正的key
            String realKey=keyPrefix.getPrefix()+String.valueOf(key);
            return jedis.exists(realKey);
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
    }

    //增加一个值
    public <T> long incr(KeyPrefix keyPrefix,Object key){
        Jedis jedis=null;
        try{
            jedis = jedisPool.getResource();
            //生成真正的key
            String realKey=keyPrefix.getPrefix()+String.valueOf(key);
            return jedis.incr(realKey);
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
    }

    //减少一个值
    public <T> long decr(KeyPrefix keyPrefix,Object key){
        Jedis jedis=null;
        try{
            jedis = jedisPool.getResource();
            //生成真正的key
            String realKey=keyPrefix.getPrefix()+String.valueOf(key);
            return jedis.decr(realKey);
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
    }

    //删除一个key
    public boolean deleteKey(KeyPrefix keyPrefix,Object key){
        Jedis jedis=null;
        try{
            jedis = jedisPool.getResource();
            //生成真正的key
            String realKey=keyPrefix.getPrefix()+String.valueOf(key);
            return jedis.del(realKey)==1?true:false;
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
    }

    /**
     * 吧一个bean对象转化成一个字符串
     * @param value
     * @return
     */
    private <T> String beanToString(T value) {
        if(value==null){
            return null;
        }

        Class<?> clazz = value.getClass();
        if(clazz==int.class || clazz==Integer.class){
            return String.valueOf(value);
        }else if(clazz==String.class){
            return (String)value;
        }else if(clazz==long.class || clazz==Long.class){
            return String.valueOf(value);
        }else if(clazz==List.class || clazz== Map.class){
            return JSON.toJSONString(value,true); // List,map转json
        } else{
            return JSON.toJSONString(value);
        }

    }

    /**
     * 吧一个字符串转化成一个bean对象
     * @param <T>
     * @param str
     * @param clazz
     * @return
     */
    private <T> T stringToBean(String str, Class<T> clazz) {
        if(str==null || str.length()<=0 || clazz==null){
            return null;
        }

        if(clazz==int.class || clazz==Integer.class){
            return (T)Integer.valueOf(str);
        }else if(clazz==String.class){
            return (T)str;
        }else if(clazz==long.class || clazz==Long.class){
            return (T)Long.valueOf(str);
        }else{
            return JSON.toJavaObject(JSON.parseObject(str),clazz);
        }

    }


    /**
     * 字符串转List
     * @param str
     * @param clazz
     * @param <T>
     * @return
     */
    private <T> List<T> stringToList(String str, Class<T> clazz) {
        return JSON.parseArray(str,clazz);
    }

}
