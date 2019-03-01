package com.lyj.controller;

import com.lyj.model.Result;
import com.lyj.model.URL;
import com.lyj.model.linkModel.User_HotUrl;
import com.lyj.service.HotUrlService;
import com.lyj.util.PageEntity;
import com.lyj.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Administrator on 2019/2/24.
 */

@RestController
@RequestMapping("/hotUrl")
public class HotUrlController {

    @Autowired
    HotUrlService hotUrlService;

    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @RequestMapping("/incrClickNumber")
    public Result incrClickNumber(URL url,String time) throws ParseException {
        url.setShareTime(simpleDateFormat.parse(time));

        hotUrlService.incrClickNumber(url);
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
    public Result incrGoodNumber(URL url,String time) throws ParseException {
        url.setShareTime(simpleDateFormat.parse(time));

        hotUrlService.incrGoodNumber(url);
        return ResultUtil.success("点赞成功，数据将在1分钟之后更新！");
    }

    @RequestMapping("/markIsIncredGoodNumber")
    public Result markIsIncredGoodNumber(User_HotUrl user_hotUrl){
        hotUrlService.markIsIncredGoodNumber(user_hotUrl);
        return ResultUtil.success(null);
    }


    @RequestMapping("/getHotUrlByHot")
    public PageEntity<URL> getHotUrlByHot(Integer page){
        int size=10;
        List urls = hotUrlService.getHotUrlByHotByRedis(page, size);
        if(urls.size()>size){
            return new PageEntity<URL>(Long.valueOf(urls.size()),urls,urls.size()/size);
        }else{
            return new PageEntity<URL>(Long.valueOf(urls.size()),urls,1);
        }
    }




}
