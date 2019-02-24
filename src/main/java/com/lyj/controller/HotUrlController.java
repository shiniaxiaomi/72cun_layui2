package com.lyj.controller;

import com.lyj.model.HotUrl;
import com.lyj.model.Result;
import com.lyj.model.linkModel.User_HotUrl;
import com.lyj.service.HotUrlService;
import com.lyj.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        int i = hotUrlService.incrClickNumber(urlId);
        if(i==0){//如果等于0，说明数据库中还没有数据，应该新建
            HotUrl hotUrl = new HotUrl(urlId,1,0);
            hotUrlService.addHotUrl(hotUrl);
        }

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
        int i = hotUrlService.incrGoodNumber(urlId);
        if(i==0){//如果等于0，说明数据库中还没有数据，应该新建
            HotUrl hotUrl = new HotUrl(urlId,0,1);
            hotUrlService.addHotUrl(hotUrl);
        }

        return ResultUtil.success(null);
    }

    @RequestMapping("/markIsIncredGoodNumber")
    public Result markIsIncredGoodNumber(User_HotUrl user_hotUrl){
        hotUrlService.markIsIncredGoodNumber(user_hotUrl);
        return ResultUtil.success(null);
    }





}
