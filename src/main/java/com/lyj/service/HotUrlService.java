package com.lyj.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lyj.config.RedisConfig;
import com.lyj.dao.HotUrlDao;
import com.lyj.exception.MessageException;
import com.lyj.model.HotUrl;
import com.lyj.model.URL;
import com.lyj.model.linkModel.User_HotUrl;
import com.lyj.util.PublicVar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Created by Administrator on 2019/2/24.
 */

@Service
public class HotUrlService {

    @Autowired
    HotUrlDao hotUrlDao;

    @Autowired
    URLService urlService;

    @Autowired
    RedisTemplate redisTemplate;

    public static int clickNumber=0;
    public static int goodNumber=0;

//    @PostConstruct
//    public void init(){
//        redisTemplate.opsForValue().set("4",4);
//    }

//    public PageInfo<URL> getHotUrlByHot(Integer page, int limit) {
//        PageHelper.startPage(page, limit);
//        List<URL> urls = hotUrlDao.getHotUrlByHot();
//        return new PageInfo<>(urls);
//    }

    //之后直接从redis获取数据
    public void getHotUrlFromRedis(){

    }

    //定时每10分钟更新redis中的数据
    //不需要实时，大概10分钟更新一次即可，更新成功之后放入redis中，之后请求就直接请求redis
    public List getHotUrlByHot(Integer page, int limit) {
        Set set = redisTemplate.opsForZSet().reverseRange(PublicVar.hotUrlScore, (page - 1) * limit, limit - 1);
        List<Object> ids = Arrays.asList(set.toArray());

        List list = redisTemplate.executePipelined(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                for (int i = 0; i < ids.size(); i++) {
                    //这个进行单个命令操作，外面使用for循环实现批量操作
                    connection.hMGet(String.valueOf(ids.get(i)).getBytes(),"clickNumber".getBytes(),"goodNumber".getBytes());
                }
                return null;
            }
        });
        List<URL> urls = urlService.getUrlsByIdBatch(ids);

        for(int i=0;i<set.size();i++){
            URL url = urls.get(i);
            Object clickNumber=((ArrayList)list.get(i)).get(0);
            Object goodNumber=((ArrayList)list.get(i)).get(1);
            url.setGoodNumber(clickNumber==null?0:(int)clickNumber);
            url.setClickNumber(goodNumber==null?0:(int)goodNumber);
        }

        return urls;
    }



//    public void addHotUrl(HotUrl hotUrl) {
//        int i = hotUrlDao.addHotUrl(hotUrl);
//        if(i!=1){
//            throw new MessageException("共享网址热点数据添加失败!");
//        }
//    }




    //浏览量加1
    public void incrClickNumber(int urlId) {
        //如果map不存在，则自动创建。如果map中的key不存在，则自动创建，并初始化值为0
        redisTemplate.opsForHash().increment(String.valueOf(urlId), "clickNumber", 1);//在urlId对应的map中添加点击量
        redisTemplate.opsForZSet().incrementScore(PublicVar.hotUrlScore, String.valueOf(urlId),PublicVar.clickValue);//在分数中增加分值
    }

    //收藏量加1
    public void incrGoodNumber(int urlId) {
        redisTemplate.opsForHash().increment(String.valueOf(urlId), "goodNumber", 1);//在urlId对应的map中添加点赞量
        redisTemplate.opsForZSet().incrementScore(PublicVar.hotUrlScore, String.valueOf(urlId),PublicVar.goodValue);//在分数中增加分值
    }

    //查询用户是否已经点赞过该链接
    public int isIncredGoodNumber(User_HotUrl user_hotUrl) {
        return hotUrlDao.isIncredGoodNumber(user_hotUrl);
    }

    //标记用户点赞过的链接
    public void markIsIncredGoodNumber(User_HotUrl user_hotUrl) {
        int i = hotUrlDao.markIsIncredGoodNumber(user_hotUrl);
        if(i==0){
            throw new MessageException("点赞标记失败！");
        }
    }
}
