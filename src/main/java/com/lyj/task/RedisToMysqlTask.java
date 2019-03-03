package com.lyj.task;

import com.lyj.model.HotUrl;
import com.lyj.model.User;
import com.lyj.service.HotUrlService;
import com.lyj.service.UserService;
import com.lyj.util.PublicVar;
import com.lyj.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 定时任务：
 */
@Component
public class RedisToMysqlTask {


    Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    HotUrlService hotUrlService;

    @Autowired
    UserService userService;


    //用法
//    @Scheduled(fixedRate = 5000) ：上一次开始执行时间点之后5秒再执行
//    @Scheduled(fixedDelay = 5000) ：上一次执行完毕时间点之后5秒再执行
//    @Scheduled(initialDelay=1000, fixedRate=5000) ：第一次延迟1秒后执行，之后按fixedRate的规则每5秒执行一次
//    @Scheduled(cron="*/5 * * * * *") ：通过cron表达式定义规则（每5秒执行一次）（秒，分，时，日，月，星期）
    //举例：
    // 1. *：表示匹配该域的任意值。假如在Minutes域使用*, 即表示每分钟都会触发事件。
    // 2. /：表示起始时间开始触发，然后每隔固定时间触发一次。例如在Minutes域使用5/20,则意味着5分钟触发一次，而25，45等分别触发一次.
    // 3. ,：表示列出枚举值。例如：在Minutes域使用5,20，则意味着在5和20分每分钟触发一次。

    //更新url总分
    @Scheduled(cron = "0 0 1 * * *")
    public void updateUrlScoreData(){
        logger.warn("================执行 更新url总分 的定时任务===================");

        int size=1000;//每次更新1000条
        Long number = redisTemplate.opsForZSet().size(PublicVar.urlScore);

        for(int i=0;i<(number/size)+1;i++){
            Set set = redisTemplate.opsForZSet().rangeWithScores(PublicVar.urlScore, i * size, (i + 1) * size-1);
            Object[] objects = set.toArray();
            List<HotUrl> hotUrls=new ArrayList<>();
            for(int k=0;k<objects.length;k++){
                HotUrl hotUrl=new HotUrl();
                hotUrl.setUrlId(RedisUtil.toInt(((DefaultTypedTuple)objects[k]).getValue()));
                hotUrl.setScore(((DefaultTypedTuple)objects[k]).getScore());
                hotUrls.add(hotUrl);
            }
            int updateNumber = hotUrlService.updateHotUrlByUrlIdBatch(hotUrls);
            logger.warn("更新了"+updateNumber+"条数据");
        }

        logger.warn("更新了"+number+"条数据");
        logger.warn("================ 更新url总分 定时任务执行完成===================");
    }

    //更新点击量
    @Scheduled(cron = "0 0 2 * * *")
    public void updateClickNumberData(){
        logger.warn("================执行 点击量更新 的定时任务===================");
        List<HotUrl> list=new ArrayList();
        Cursor<Map.Entry<Object,Object>> scan = redisTemplate.opsForHash().scan(PublicVar.urlClickNumber, ScanOptions.scanOptions().count(1).build());
        Long count=0L;
        int i=0;
        while (scan.hasNext()){
            count++;//统计总数
            Map.Entry<Object,Object> entry = scan.next();
            HotUrl hotUrl=new HotUrl();
            hotUrl.setUrlId(RedisUtil.toInt(entry.getKey()));
            hotUrl.setClickNumber(RedisUtil.toInt(entry.getValue()));
            list.add(hotUrl);
            i++;
            if(i>=1000){
                int number = hotUrlService.updateHotUrlClickNumberByUrlIdBatch(list);
                logger.warn("更新了"+number+"条记录");
                list.clear();
                i=0;
            }
        }
        int number = hotUrlService.updateHotUrlClickNumberByUrlIdBatch(list);
        logger.warn("更新了"+number+"条记录");
        logger.warn("一共更新了"+count+"条记录");
        logger.warn("================ 点击量更新 定时任务执行完成===================");
    }

    //更新点赞量
    @Scheduled(cron = "0 0 3 * * *")
    public void updateGoodNumberData(){
        logger.warn("================执行 点赞量更新 的定时任务===================");
        List<HotUrl> list=new ArrayList();
        Cursor<Map.Entry<Object,Object>> scan = redisTemplate.opsForHash().scan(PublicVar.urlGoodNumber, ScanOptions.scanOptions().count(1).build());
        Long count=0L;
        int i=0;
        while (scan.hasNext()){
            count++;//统计总数
            Map.Entry<Object,Object> entry = scan.next();
            HotUrl hotUrl=new HotUrl();
            hotUrl.setUrlId(RedisUtil.toInt(entry.getKey()));
            hotUrl.setGoodNumber(RedisUtil.toInt(entry.getValue()));
            list.add(hotUrl);
            i++;
            if(i>=1000){
                int number = hotUrlService.updateHotUrlGoodNumberByUrlIdBatch(list);
                logger.warn("更新了"+number+"条记录");
                list.clear();
                i=0;
            }
        }
        int number = hotUrlService.updateHotUrlGoodNumberByUrlIdBatch(list);
        logger.warn("更新了"+number+"条记录");
        logger.warn("一共更新了"+count+"条记录");
        logger.warn("================ 点赞量更新 定时任务执行完成===================");
    }

    @Scheduled(cron = "0 0 4 * * *")
    public void updateUserShareNumberData(){
        logger.warn("================执行 用户分享数量更新 的定时任务===================");
        int size=1000;//每次更新1000条
        Long number = redisTemplate.opsForZSet().size(PublicVar.userShareScore);
        for(int i=0;i<(number/size)+1;i++){
            Set set = redisTemplate.opsForZSet().rangeWithScores(PublicVar.userShareScore, i * size, (i + 1) * size-1);
            Object[] objects = set.toArray();

            List<User> userList=new ArrayList<>();
            for(int k=0;k<objects.length;k++){
                User user=new User();
                user.setUserName((String)((DefaultTypedTuple)objects[k]).getValue());
                user.setShareNumber(RedisUtil.toInt(((DefaultTypedTuple)objects[k]).getScore()));
                userList.add(user);
            }
            int updateNumber = userService.updateUserShareNumberByUserNameBatch(userList);
            logger.warn("user表中更新了"+updateNumber+"条数据");
        }
        logger.warn("一共更新了"+number+"条记录");
        logger.warn("================ 用户分享数量更新 定时任务执行完成===================");
    }

    @Scheduled(cron = "0 0 5 * * *")
    public void updateUserGoodNumberData(){
        logger.warn("================执行 用户获赞数量更新 的定时任务===================");
        int size=1000;//每次更新1000条
        Long number = redisTemplate.opsForZSet().size(PublicVar.userGoodScore);
        for(int i=0;i<(number/size)+1;i++){
            Set set = redisTemplate.opsForZSet().rangeWithScores(PublicVar.userGoodScore, i * size, (i + 1) * size-1);
            Object[] objects = set.toArray();

            List<User> userList=new ArrayList<>();
            for(int k=0;k<objects.length;k++){
                User user=new User();
                user.setUserName((String)((DefaultTypedTuple)objects[k]).getValue());
                user.setGoodNumber(RedisUtil.toInt(((DefaultTypedTuple)objects[k]).getScore()));
                userList.add(user);
            }
            int updateNumber = userService.updateUserGoodNumberByUserNameBatch(userList);
            logger.warn("更新了"+updateNumber+"条数据");
        }
        logger.warn("一共更新了"+number+"条记录");
        logger.warn("================ 用户获赞数量更新 定时任务执行完成===================");
    }

}