package com.lyj.task;

import com.lyj.model.HotUrl;
import com.lyj.service.HotUrlService;
import com.lyj.util.PublicVar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 定时任务：
 */
@Component
public class RedisToMysqlTask {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    HotUrlService hotUrlService;


    //用法
//    @Scheduled(fixedRate = 5000) ：上一次开始执行时间点之后5秒再执行
//    @Scheduled(fixedDelay = 5000) ：上一次执行完毕时间点之后5秒再执行
//    @Scheduled(initialDelay=1000, fixedRate=5000) ：第一次延迟1秒后执行，之后按fixedRate的规则每5秒执行一次
//    @Scheduled(cron="*/5 * * * * *") ：通过cron表达式定义规则（每5秒执行一次）（秒，分，时，日，月，星期）
    //举例：
    // 1. *：表示匹配该域的任意值。假如在Minutes域使用*, 即表示每分钟都会触发事件。
    // 2. /：表示起始时间开始触发，然后每隔固定时间触发一次。例如在Minutes域使用5/20,则意味着5分钟触发一次，而25，45等分别触发一次.
    // 3. ,：表示列出枚举值。例如：在Minutes域使用5,20，则意味着在5和20分每分钟触发一次。

    //将热点网址的点击数据和点赞数据保存到数据库中
    //每天1点执行一次
    @Scheduled(cron = "0 0 1 * * *")
    public void updateHotUrlData(){
        System.out.println("================执行从redis拉取数据到mysql的定时任务===================");

        int size=1000;//每次更新1000条
        Long number = redisTemplate.opsForZSet().size(PublicVar.hotUrlScore);

        for(int i=0;i<(number/size)+1;i++){
            Set set = redisTemplate.opsForZSet().rangeWithScores(PublicVar.hotUrlScore, i * size, (i + 1) * size-1);
            Object[] objects = set.toArray();

            //在redis中批量获取点击量和点赞量
            List list = redisTemplate.executePipelined(new RedisCallback<String>() {
                @Override
                public String doInRedis(RedisConnection connection) throws DataAccessException {
                    for(int j=0;j<objects.length;j++){
                        int urlId= (int) ((DefaultTypedTuple)objects[j]).getValue();
                        connection.hGet(String.valueOf(urlId).getBytes(),"clickNumber".getBytes());
                        connection.hGet(String.valueOf(urlId).getBytes(),"goodNumber".getBytes());
                    }
                    return null;
                }
            });

            List<HotUrl> hotUrls=new ArrayList<>();
            for(int k=0;k<objects.length;k++){
                HotUrl hotUrl=new HotUrl();
                hotUrl.setUrlId((int)((DefaultTypedTuple)objects[k]).getValue());
                hotUrl.setScore(((DefaultTypedTuple)objects[k]).getScore());
                hotUrl.setClickNumber((int)list.get(2*k));
                hotUrl.setGoodNumber((int)list.get(2*k+1));
                hotUrls.add(hotUrl);
            }

            //批量更新hotUrl
            hotUrlService.updateHotUrlByUrlIdBatch(hotUrls);

        }

        System.out.println("================定时任务执行完成===================");

    }
}