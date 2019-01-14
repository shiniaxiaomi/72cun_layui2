package com.lyj.controller;

import com.lyj.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

/**
 * Created by Yingjie.Lu on 2018/10/7.
 */

@Controller
public class PageController {

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

    @RequestMapping("/admin/userManager")
    public ModelAndView userManager(ModelAndView mv){
        mv.setViewName("admin/userManager");
        return mv;
    }

    @RequestMapping("/searchUrl")
    public ModelAndView searchUrl(ModelAndView mv){
        mv.setViewName("searchUrl");
        return mv;
    }

    @RequestMapping("/folderManager")
    public ModelAndView urlManager(ModelAndView mv){
        mv.setViewName("folderManager");
        return mv;
    }





}
