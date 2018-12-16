package com.lyj.controller;

import com.lyj.service.RedisService;
import com.lyj.model.Result;
import com.lyj.redisKey.FolderKey;
import com.lyj.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by 陆英杰
 * 2018/12/16 21:29
 */

@Controller
public class RedisController {

    @Autowired
    RedisService redisService;


    @RequestMapping("/redis/get")
    @ResponseBody
    public Result redisGet(){
        String k1 = redisService.get(FolderKey.getByUserId,"1", String.class);

        return ResultUtil.success(k1);
    }

    @RequestMapping("/redis/set")
    @ResponseBody
    public Result redisSet(){
        redisService.set(FolderKey.getByUserId,"1", "2323232");
        String k2 = redisService.get(FolderKey.getByUserId,"1", String.class);
        return ResultUtil.success(k2);
    }

}
