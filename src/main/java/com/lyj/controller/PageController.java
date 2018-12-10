package com.lyj.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by Yingjie.Lu on 2018/10/7.
 */

@Controller
public class PageController {




    @RequestMapping("/tree")
    public ModelAndView tree(ModelAndView mv){
        mv.setViewName("tree");
        return mv;
    }
    @RequestMapping("/register")
    public ModelAndView register(ModelAndView mv){
        mv.setViewName("register");
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
