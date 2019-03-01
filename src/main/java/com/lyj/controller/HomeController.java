package com.lyj.controller;

import com.github.pagehelper.PageInfo;
import com.lyj.model.HotUrl;
import com.lyj.model.Result;
import com.lyj.model.URL;
import com.lyj.model.User;
import com.lyj.service.HotUrlService;
import com.lyj.service.URLService;
import com.lyj.service.UserService;
import com.lyj.util.PageEntity;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/2/17.
 */

@RestController
@RequestMapping("/home")
public class HomeController {

    @Autowired
    URLService urlService;

    @Autowired
    UserService userService;

    @Autowired
    HotUrlService hotUrlService;

    @Autowired
    RedisTemplate redisTemplate;

    //个人主页
    @RequestMapping("/{userName}")
    public ModelAndView personalPage(@PathVariable(value="userName") String userName){

        ModelAndView mv=new ModelAndView("personalPage");

        User user = userService.getUserByUserName(userName);
        if(user==null) {
            mv.setViewName("/");//返回登入页
            return mv;
        }else{
            mv.addObject("userName",userName);
            mv.addObject("userId",user.getId());
            return mv;
        }
    }


    //会查询出最新的点击量和点赞量
    @RequestMapping("/getShareUrlsLike")
    public PageEntity<URL> getShareUrlsLike(String keywords,Integer userId,Integer page){
        PageInfo<URL> pageInfo=null;
        if(userId==null){
            pageInfo = urlService.getShareUrlsLike(keywords, page, 10);
        }else{
            pageInfo = urlService.getShareUrlsByUserIdLike(keywords,userId, page, 10);
        }

        List<URL> urls = pageInfo.getList();

        //从redis中查询每个url的点击量和点赞量，并替换
        List list = redisTemplate.executePipelined(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                for (int i = 0; i < urls.size(); i++) {
                    //这个进行单个命令操作，外面使用for循环实现批量操作
                    connection.hMGet(String.valueOf(urls.get(i).getId()).getBytes(),"clickNumber".getBytes(),"goodNumber".getBytes());
                }
                return null;
            }
        });

        //从redis中查询点击量和点赞量，并替换
        for(int i=0;i<urls.size();i++){
            Object clickNumber = ((List)list.get(i)).get(0);
            Object goodNumber = ((List)list.get(i)).get(1);
            urls.get(i).setClickNumber(clickNumber==null?0:(int)clickNumber);
            urls.get(i).setGoodNumber(goodNumber==null?0:(int)goodNumber);
        }

        return new PageEntity<>(pageInfo.getTotal(),urls,pageInfo.getPages());//直接放入组装好的urls
    }






}
