package com.lyj.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.github.pagehelper.PageHelper;
import com.lyj.dao.HotUrlDao;
import com.lyj.exception.MessageException;
import com.lyj.model.HotUrl;
import com.lyj.model.URL;
import com.lyj.model.linkModel.User_HotUrl;
import com.lyj.util.PageEntity;
import com.lyj.util.PublicVar;
import com.lyj.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

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
    private Object count;
    private Object hotUrlByPage;

//    public List<URL> getHotUrlByHot(Integer page, int limit) {
//        PageHelper.startPage(page, limit);
//        List<URL> urls = hotUrlDao.getHotUrlByHot();
//        return urls;
//    }

    //查询出的结果会缓存在redis中，并1分钟后过期，过期后会自动重新查询
    public PageEntity getHotUrlByHotByRedis(Integer page, int limit) {
        PageEntity<URL> pageEntity=null;

        Object data = redisTemplate.opsForValue().get(PublicVar.hotUrlData + page);
        if(data!=null){
            return JSON.parseObject((String) data, new TypeReference<PageEntity<URL>>() {});
        }else{
            Set set = redisTemplate.opsForZSet().reverseRange(PublicVar.urlScore, (page - 1) * limit, page*limit - 1);//获取排序且分页的url的id集合
            if(set.size()==0){//如果没有结果集，则直接返回
                return new PageEntity<>(0L, new ArrayList<URL>(), page);
            }

            Object[] ids = set.toArray();
            List list = redisTemplate.executePipelined(new RedisCallback<String>() {
                @Override
                public String doInRedis(RedisConnection connection) throws DataAccessException {
                    for(int i=0;i<ids.length;i++){
                        connection.hGet(PublicVar.urlClickNumber.getBytes(),RedisUtil.toByte(ids[i]));
                        connection.hGet(PublicVar.urlGoodNumber.getBytes(),RedisUtil.toByte(ids[i]));
                    }
                    return null;
                }
            });
            List<URL> urlList = urlService.getUrlsByIdBatch(Arrays.asList(ids));
            for(int i=0;i<urlList.size();i++){
                URL url = urlList.get(i);
                url.setClickNumber(RedisUtil.toInt(list.get(2*i)));
                url.setGoodNumber(RedisUtil.toInt(list.get(2*i+1)));
            }
            pageEntity = new PageEntity<>(Long.valueOf(urlList.size()), urlList, set.size()<limit?page:page+1);
            if(PublicVar.updateTime>0){
                redisTemplate.opsForValue().set(PublicVar.hotUrlData+page,JSON.toJSONString(pageEntity),PublicVar.updateTime, TimeUnit.MINUTES);//在将数据缓存在redis中,并并且设置1分钟过期
            }
        }

        return pageEntity;
    }

//    public void incrRedisUtil(RedisTemplate redisTemplate){
//
//    }

    //浏览量加1
    public void incrClickNumber(URL url) {
        //增加点击量
        //在redis中进行批量操作
        redisTemplate.executePipelined(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                //如果map不存在，则自动创建。如果map中的key不存在，则自动创建，并初始化值为0
                connection.hIncrBy(PublicVar.urlClickNumber.getBytes(),RedisUtil.toByte(url.getId()),1L);//urlClickNumber
                connection.zIncrBy(PublicVar.urlScore.getBytes(),PublicVar.clickValue,RedisUtil.toByte(url.getId()));//在分数中增加分值
                return null;
            }
        });

    }

    //收藏量加1
    public void incrGoodNumber(URL url,String userName) {

        //增加点赞量
        //在redis中进行批量操作
        redisTemplate.executePipelined(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                //如果map不存在，则自动创建。如果map中的key不存在，则自动创建，并初始化值为0
                connection.hIncrBy(PublicVar.urlGoodNumber.getBytes(),RedisUtil.toByte(url.getId()),1L);//urlGoodNumber
                connection.zIncrBy(PublicVar.urlScore.getBytes(),PublicVar.goodValue,RedisUtil.toByte(url.getId()));//urlScore
                connection.zIncrBy(PublicVar.userGoodScore.getBytes(),1.0,RedisUtil.toByte(userName));//userGoodScore
                return null;
            }
        });

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

    public int deleteHotUrlByUrlId(Integer id) {
        return hotUrlDao.deleteHotUrlByUrlId(id);
    }

    public int updateHotUrlByUrlIdBatch(List<HotUrl> hotUrls) {
        if(hotUrls.size()>0){
            return hotUrlDao.updateHotUrlByUrlIdBatch(hotUrls);
        }else{
            return 0;
        }
    }

    public int getCount() {
        return hotUrlDao.getCount();
    }

    public List<URL> getHotUrlByPage(int page,int limit) {
        PageHelper.startPage(page, limit);
        return hotUrlDao.getHotUrlByPage();
    }


    public int deleteHotUrlByUrlIdBatch(List<Integer> ids) {
        if(ids.size()>0){
            return  hotUrlDao.deleteHotUrlByUrlIdBatch(ids);
        }else {
            return 0;
        }

    }


    public int updateHotUrlClickNumberByUrlIdBatch(List<HotUrl> list) {
        if(list.size()>0){
            return hotUrlDao.updateHotUrlClickNumberByUrlIdBatch(list);
        }else{
            return 0;
        }
    }

    public int updateHotUrlGoodNumberByUrlIdBatch(List<HotUrl> list) {
        if(list.size()>0){
            return hotUrlDao.updateHotUrlGoodNumberByUrlIdBatch(list);
        }else{
            return 0;
        }
    }
}
