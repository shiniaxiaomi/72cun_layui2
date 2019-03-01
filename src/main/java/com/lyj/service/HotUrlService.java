package com.lyj.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lyj.dao.HotUrlDao;
import com.lyj.exception.MessageException;
import com.lyj.model.HotUrl;
import com.lyj.model.URL;
import com.lyj.model.linkModel.User_HotUrl;
import com.lyj.util.PublicVar;
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

    public List<URL> getHotUrlByHot(Integer page, int limit) {
        PageHelper.startPage(page, limit);
        List<URL> urls = hotUrlDao.getHotUrlByHot();
        return urls;
    }

    //查询出的结果会缓存在redis中，并1分钟后过期，过期后会自动重新查询
    public List getHotUrlByHotByRedis(Integer page, int limit) {

        //先去redis中获取主页缓存数据
        Object homeData = redisTemplate.opsForValue().get("homeData_page"+page);

        List<URL> urls=null;
        if(homeData!=null){
            urls = JSON.parseArray((String) homeData, URL.class);
        }else if(homeData==null){//如果数据为空或者过期，则重新计算并获取

            Set set = redisTemplate.opsForZSet().reverseRange(PublicVar.hotUrlScore, (page - 1) * limit, limit - 1);

            List<Object> ids = Arrays.asList(set.toArray());
            List list = redisTemplate.executePipelined(new RedisCallback<String>() {
                @Override
                public String doInRedis(RedisConnection connection) throws DataAccessException {
                    for (int i = 0; i < ids.size(); i++) {
                        //这个进行单个命令操作，外面使用for循环实现批量操作
                        connection.hGetAll(String.valueOf(ids.get(i)).getBytes());
                    }
                    return null;
                }
            });

            urls = new ArrayList<>();//保存url的集合
            for(int i=0;i<set.size();i++){
                Object clickNumber=((Map)list.get(i)).get("clickNumber");
                Object goodNumber=((Map)list.get(i)).get("goodNumber");
                Object json= ((Map)list.get(i)).get("url");

                if(json!=null){
                    URL url = JSON.parseObject((String)json, URL.class);
                    url.setClickNumber(clickNumber==null?0:Integer.valueOf((String)clickNumber));
                    url.setGoodNumber(goodNumber==null?0:Integer.valueOf((String)goodNumber));
                    urls.add(url);
                }
            }

            redisTemplate.opsForValue().set("homeData_page"+page,JSON.toJSONString(urls),PublicVar.updateTime, TimeUnit.MINUTES);//在将数据缓存在redis中,并并且设置1分钟过期
        }

        return urls;
    }

    //浏览量加1
    public void incrClickNumber(URL url) {

        //增加点击量
        //在redis中进行批量操作
        redisTemplate.executePipelined(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                //如果map不存在，则自动创建。如果map中的key不存在，则自动创建，并初始化值为0
                connection.hIncrBy(String.valueOf(url.getId()).getBytes(),"clickNumber".getBytes(),1L);//在urlId对应的map中添加点击量
                connection.hSetNX(String.valueOf(url.getId()).getBytes(), "url".getBytes(),JSON.toJSONString(url).getBytes());//保存url信息
                connection.zIncrBy(PublicVar.hotUrlScore.getBytes(),PublicVar.clickValue,String.valueOf(url.getId()).getBytes());//在分数中增加分值
                return null;
            }
        });

    }

    //收藏量加1
    public void incrGoodNumber(URL url) {

        //增加点赞量
        //在redis中进行批量操作
        redisTemplate.executePipelined(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                //如果map不存在，则自动创建。如果map中的key不存在，则自动创建，并初始化值为0
                connection.hIncrBy(String.valueOf(url.getId()).getBytes(),"goodNumber".getBytes(),1L);//在urlId对应的map中添加点击量
                connection.hSetNX(String.valueOf(url.getId()).getBytes(), "url".getBytes(),JSON.toJSONString(url).getBytes());//保存url信息
                connection.zIncrBy(PublicVar.hotUrlScore.getBytes(),PublicVar.goodValue,String.valueOf(url.getId()).getBytes());//在分数中增加分值
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

    public void deleteHotUrlByUrlId(Integer id) {
        int i=hotUrlDao.deleteHotUrlByUrlId(id);
        if(i==0){
            throw new MessageException("热点数据删除失败！");
        }
    }

    public void updateHotUrlByUrlIdBatch(List<HotUrl> hotUrls) {
        int i=hotUrlDao.updateHotUrlByUrlIdBatch(hotUrls);
    }

    public int getCount() {
        return hotUrlDao.getCount();
    }

    public List<URL> getHotUrlByPage(int page,int limit) {
        PageHelper.startPage(page, limit);
        return hotUrlDao.getHotUrlByPage();
    }

    public void deleteUser_HotUrlByUrlId(Integer urlId) {
        int i = hotUrlDao.deleteUser_HotUrlByUrlId(urlId);
        if(i==0){
            throw new MessageException("网址点赞记录删除失败!");
        }
    }
}
