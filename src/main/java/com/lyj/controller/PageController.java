package com.lyj.controller;

import com.lyj.model.User;
import com.lyj.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

/**
 * Created by Yingjie.Lu on 2018/10/7.
 */

@Controller
public class PageController {

    @Autowired
    MessageService messageService;

    //网站首页路由
    @RequestMapping("/homePage")
    public ModelAndView homePage(ModelAndView mv, HttpSession session){
        User user = (User) session.getAttribute("user");
        mv.setViewName("homePage");
        mv.addObject("user",user);
        return mv;
    }

    //后台管理系统的路由
    @RequestMapping("/admin")
    public ModelAndView admin(ModelAndView mv, HttpSession session){
        User user = (User) session.getAttribute("user");
        if(user==null){
            mv.setViewName("admin/index");
        }else if("陆英杰".equals(user.getUserName())){
            mv.setViewName("admin/main");
        }else{
            mv.setViewName("/");
        }
        return mv;
    }

    @RequestMapping("/membership")
    public ModelAndView membership(ModelAndView mv){
        mv.setViewName("membership");
        return mv;
    }

    @RequestMapping("/admin/userManager")
    public ModelAndView userManager(ModelAndView mv){
        mv.setViewName("admin/userManager");
        return mv;
    }

    @RequestMapping("/admin/getUserUrlDetail")
    public ModelAndView getUserUrl(int userId){
        ModelAndView mv=new ModelAndView("admin/userUrlDetail");
        mv.addObject("userId",userId);
        return mv;
    }

    @RequestMapping("/userHome")
    public String userHome(HttpSession session){
        if(session.getAttribute("user")!=null){
            return "forward:/main";
        }else{
            return "forward:/toLogin";
        }
    }

    @RequestMapping("/main")
    public ModelAndView userMain(ModelAndView mv, HttpSession session,User sessionUser){

        mv.setViewName("main");
        mv.addObject("user",session.getAttribute("user"));

        if(sessionUser.getUserName().equals("陆英杰")){
            int count = messageService.getSendedMessagesCount();//获取所有管理员未读信息的总数
            mv.addObject("count",count);//添加未处理消息的总数
        }else{
            int count = messageService.getMessagesCount(sessionUser);//获取所有用户未读信息的总数
            mv.addObject("count",count);//添加未处理消息的总数
        }
        return mv;
    }

    //转发到登入页
    @RequestMapping("/toLogin")
    public String toLogin(){
        return "/toLogin";
    }










}
