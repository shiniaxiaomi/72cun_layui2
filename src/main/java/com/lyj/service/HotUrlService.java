package com.lyj.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lyj.dao.HotUrlDao;
import com.lyj.exception.MessageException;
import com.lyj.model.HotUrl;
import com.lyj.model.URL;
import com.lyj.model.linkModel.User_HotUrl;
import com.lyj.util.PublicVar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created by Administrator on 2019/2/24.
 */

@Service
public class HotUrlService {

    @Autowired
    HotUrlDao hotUrlDao;

//    @Autowired
//    JedisPool jedisPool;

    @Autowired
    RedisTemplate redisTemplate;

    public static int clickNumber=0;
    public static int goodNumber=0;

    @PostConstruct
    public void init(){
        System.out.println(redisTemplate);
    }

    public PageInfo<URL> getHotUrlByHot(Integer page, int limit) {

//        Long count = redisTemplate.opsForZSet().zCard(PublicVar.redisHotUrl);

//        Jedis jedis = jedisPool.getResource();
//        Long count = jedis.zcard(PublicVar.redisHotUrl);
//        if(count<=50)

        PageHelper.startPage(page, limit);
        List<URL> urls = hotUrlDao.getHotUrlByHot();
        return new PageInfo<>(urls);
    }

    public int incrClickNumber(int urlId) {
//        Jedis jedis = jedisPool.getResource();
//        Double score = jedis.zincrby(PublicVar.redisHotUrl, PublicVar.clickValue, String.valueOf(urlId));//在redis中增加分值，并返回总数
//        number++;
//        if(number>100){
//            number=0;
//            hotUrlDao.updateClickNumer();//当number
//        }

//        jedis.zadd
//        List<String> mget = jedis.mget("1", "2");
//        jedis.mset()

//        List<URL> urls=new ArrayList<>();
//        List<String> list = jedis.hmget(PublicVar.redisHotUrl, "1", "2");
//        for(String json:list){
//            URL url = JSON.parseObject(json, URL.class);
//            urls.add(url);
//        }


        return hotUrlDao.incrClickNumber(urlId);
    }

    public void addHotUrl(HotUrl hotUrl) {
        int i = hotUrlDao.addHotUrl(hotUrl);
        if(i!=1){
            throw new MessageException("共享网址热点数据添加失败!");
        }
    }

    public void deleteHotUrl(){

    }

    public int isIncredGoodNumber(User_HotUrl user_hotUrl) {
        return hotUrlDao.isIncredGoodNumber(user_hotUrl);
    }

    public int incrGoodNumber(int urlId) {
        return hotUrlDao.incrGoodNumber(urlId);
    }

    public void markIsIncredGoodNumber(User_HotUrl user_hotUrl) {
        int i = hotUrlDao.markIsIncredGoodNumber(user_hotUrl);
        if(i==0){
            throw new MessageException("点赞标记失败！");
        }
    }
}
