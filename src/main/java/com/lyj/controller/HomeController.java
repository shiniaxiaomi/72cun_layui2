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

    @RequestMapping("/getShareUrlsLike")
    public PageEntity<URL> getShareUrlsLike(String keywords,Integer userId,Integer page){
        PageInfo<URL> pageInfo=null;
        if(userId==null){
            pageInfo = urlService.getShareUrlsLike(keywords, page, 10);
        }else{
            pageInfo = urlService.getShareUrlsByUserIdLike(keywords,userId, page, 10);
        }
        return new PageEntity<>(pageInfo.getTotal(),pageInfo.getList(),pageInfo.getPages());//直接放入组装好的urls
    }






}
