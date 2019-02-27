package com.lyj.controller;

import com.github.pagehelper.PageInfo;
import com.lyj.model.HotUrl;
import com.lyj.model.Result;
import com.lyj.model.URL;
import com.lyj.model.linkModel.User_HotUrl;
import com.lyj.service.HotUrlService;
import com.lyj.util.PageEntity;
import com.lyj.util.PublicVar;
import com.lyj.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2019/2/24.
 */

@RestController
@RequestMapping("/hotUrl")
public class HotUrlController {

    @Autowired
    HotUrlService hotUrlService;

    @RequestMapping("/incrClickNumber")
    public Result incrClickNumber(int urlId){
        hotUrlService.incrClickNumber(urlId);
        return ResultUtil.success(null);
    }


    @RequestMapping("/isIncredGoodNumber")
    public Result isIncredGoodNumber(User_HotUrl user_hotUrl){
        int num = hotUrlService.isIncredGoodNumber(user_hotUrl);
        if(num==0){
            return ResultUtil.success(null);
        }else {
            return ResultUtil.error("一个用户只能点赞一次！");
        }
    }

    @RequestMapping("/incrGoodNumber")
    public Result incrGoodNumber(int urlId){
        hotUrlService.incrGoodNumber(urlId);
        return ResultUtil.success(null);
    }

    @RequestMapping("/markIsIncredGoodNumber")
    public Result markIsIncredGoodNumber(User_HotUrl user_hotUrl){
        hotUrlService.markIsIncredGoodNumber(user_hotUrl);
        return ResultUtil.success(null);
    }


//    @RequestMapping("/getHotUrlByHot")
//    public PageEntity<URL> getHotUrlByHot(Integer page){
//        PageInfo<URL> pageInfo = hotUrlService.getHotUrlByHot(page, 10);
//        return new PageEntity<URL>(pageInfo.getTotal(),pageInfo.getList(),pageInfo.getPages());//直接放入组装好的urls
//    }

    @RequestMapping("/getHotUrlByHot")
    public PageEntity<URL> getHotUrlByHot(Integer page){
        List urls = hotUrlService.getHotUrlByHot(page, 10);

        return new PageEntity<URL>(50L,urls,5);
    }




}
