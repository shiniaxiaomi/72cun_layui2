package com.lyj.init;

import com.alibaba.fastjson.JSON;
import com.lyj.model.URL;
import com.lyj.service.HotUrlService;
import com.lyj.util.PublicVar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Yingjie.Lu on 2019/2/28.
 */

/**
 * 热点数据初始化
 */
@Component
public class HotUrlDataInit {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    HotUrlService hotUrlService;



    // 将数据库中的hotUrl中的数据缓存在redis
    // 该方法一般不使用，因为redis在停止后会保存数据，下次启动后会自动恢复数据
    // 当服务器停电或者是关机的时候，导致数据丢失，然后就可以通过该方法将数据完整的再次缓存到数据库中
//    @PostConstruct //需要时再开启注解
    public void pullMysqlToRedis(){
        int size=1000;//每次从数据库更新到redis中的数据量
        int count = hotUrlService.getCount();

        for(int i=0;i<(count/size)+1;i++){
            List<URL> urls = hotUrlService.getHotUrlByPage(i, size);
            //在redis中批量获取点击量和点赞量
            redisTemplate.executePipelined(new RedisCallback<String>() {
                @Override
                public String doInRedis(RedisConnection connection) throws DataAccessException {
                    for(int j=0;j<urls.size();j++){
                        URL url = urls.get(j);
                        connection.hSet(String.valueOf(url.getId()).getBytes(),"clickNumber".getBytes(),String.valueOf(url.getClickNumber()).getBytes());//在urlId对应的map中添加点击量
                        connection.hSet(String.valueOf(url.getId()).getBytes(),"goodNumber".getBytes(),String.valueOf(url.getGoodNumber()).getBytes());//在urlId对应的map中添加点击量
                        connection.hSet(String.valueOf(url.getId()).getBytes(), "url".getBytes(),JSON.toJSONString(url).getBytes());//保存url信息
                        connection.zAdd(PublicVar.urlScore.getBytes(),url.getScore(),String.valueOf(url.getId()).getBytes());//在分数中增加分值
                    }
                    return null;
                }
            });
        }

    }



    //一般不使用（不开启注解，因为redis中已经存在数据）
    //当redis中的数据量小于50条时，就从数据库中将数据在项目启动的时候就保存在数据库中
//    @PostConstruct //需要时再开启注解
//    public void initDataToRedis(){
//        Long size = redisTemplate.opsForZSet().size(PublicVar.urlScore);
//        if(size<50){
//            List<URL> hotUrls = hotUrlService.getHotUrlByHot(0, 50);//数据库排序删选出前50条数据
//
//            //批量往redis中添加数据
//            redisTemplate.executePipelined(new RedisCallback<String>() {
//                @Override
//                public String doInRedis(RedisConnection connection) throws DataAccessException {
//                    for (int i = 0; i < hotUrls.size(); i++) {
//                        URL url = hotUrls.get(i);
//
//                        //在排行榜中添加数据
//                        connection.zAdd(PublicVar.urlScore.getBytes(),url.getScore(),String.valueOf(url.getId()).getBytes());
//
//                        //在redis中添加网址数据
//                        Map<byte[], byte[]> map=new HashMap();
//                        map.put("url".getBytes(), JSON.toJSONString(url).getBytes());
//                        map.put("clickNumber".getBytes(), String.valueOf(url.getClickNumber()).getBytes());
//                        map.put("goodNumber".getBytes(), String.valueOf(url.getGoodNumber()).getBytes());
//                        connection.hMSet(String.valueOf(url.getId()).getBytes(),map);
//                    }
//                    return null;
//                }
//            });
//        }
//
//        System.out.println("==================redis数据初始化完成===================");
//    }



}
