package com.lyj.controller;

import com.alibaba.fastjson.JSON;
import com.lyj.model.HotUrl;
import com.lyj.model.User;
import com.lyj.service.HotUrlService;
import com.lyj.task.RedisToMysqlTask;
import com.lyj.util.PublicVar;
import com.lyj.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Yingjie.Lu on 2019/3/1.
 */

@RequestMapping("/test")
@RestController
public class TestController {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    HotUrlService hotUrlService;

//    @Autowired
//    RedisToMysqlTask redisToMysqlTask;

    @RequestMapping("/1")
    public void test(){

//        Object o = redisTemplate.opsForHash().get("a", "1");
//        List list = redisTemplate.opsForHash().multiGet("hash", Arrays.asList("age", "name"));
//        redisTemplate.opsForHash().put("hash","object", JSON.toJSONString(new URL("111","222",1,null,1,null)));
//        Object parse = JSON.parse((String) redisTemplate.opsForHash().get("hash", "object"));

        //批量操作（同步的）
        List list = redisTemplate.executePipelined(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                for (int i = 0; i < 2; i++) {
                    //批量获取map
                    connection.hGet(("hash" + i).getBytes(), "1".getBytes());//第一个参数是key，第二个参数是map中的key
                }
                return null;
            }
        });

        System.out.println(list);

    }

    @RequestMapping("/2")
    public Object test1(){

//        redisToMysqlTask.updateUserGoodNumberData();

        return true;
    }

    @RequestMapping("/3")
    public void test3(){
        redisTemplate.opsForValue().set("1","2");
    }

}
