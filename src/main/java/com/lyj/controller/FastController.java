package com.lyj.controller;

import com.lyj.model.User;
import com.lyj.service.UserService;
import com.lyj.util.BASE64Util;
import com.lyj.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by 陆英杰
 * 2018/11/13 15:51
 */

/**
 * 点击链接,直接进行收藏
 */
@Controller
@RequestMapping("/fast")
public class FastController {


    @Autowired
    UserService userService;

    //首先都到这个请求,将所有的信息全部先保存在session中,然后在判断是否已经登入
    //在登入请求那边拿到session中的数据,并以json的格式返回给login页面,login页面根据返回的数据判断要跳转到那个快捷的请求
    //在快捷的请求中再次获取session中的数据,并且渲染到快捷页面上即可


    //快速收藏
    @RequestMapping("/collection")
    public ModelAndView collection( HttpSession session,HttpServletRequest request,
                                    @RequestParam(value = "url",required = false) String url,
                                    @RequestParam(value = "title",required = false) String title) throws UnsupportedEncodingException {

        ModelAndView mv=new ModelAndView();

        User user = (User) session.getAttribute("user");
        if(user==null){
            user = userService.tryLogin(request);
        }

        if(user==null){
            mv.setViewName("toLogin");
            mv.addObject("loginUrl","/login?type=collection");
            mv.addObject("url",url);
            mv.addObject("title",title);
        }else{
            mv.setViewName("fastCollection");
            mv.addObject("url",trans(url));
            mv.addObject("title",trans(title));
        }

        return mv;
    }

    //快速打开
    @RequestMapping("/open")
    public ModelAndView open(HttpSession session,HttpServletRequest request){
        ModelAndView mv=new ModelAndView();

        User user = (User) session.getAttribute("user");
        if(user==null){
            user = userService.tryLogin(request);
        }
        if(user==null){
            mv.setViewName("toLogin");
            mv.addObject("loginUrl","/login?type=open");
        }else{
            mv.setViewName("fastOpen");
        }
        return mv;
    }

    //进行编码转换，并判空
    public String trans(String str) throws UnsupportedEncodingException {
        if(StringUtil.isEmpty(str)){//如果为空,则不设置值
            return null;
        }else {
            return URLDecoder.decode(str,"utf-8");
        }
    }


}
