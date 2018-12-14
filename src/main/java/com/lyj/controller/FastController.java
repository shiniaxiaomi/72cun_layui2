package com.lyj.controller;

import com.lyj.model.User;
import com.lyj.util.StringUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

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

    //首先都到这个请求,将所有的信息全部先保存在session中,然后在判断是否已经登入
    //在登入请求那边拿到session中的数据,并以json的格式返回给login页面,login页面根据返回的数据判断要跳转到那个快捷的请求
    //在快捷的请求中再次获取session中的数据,并且渲染到快捷页面上即可
    @RequestMapping("/saveAndLogin")
    public ModelAndView fast(ModelAndView mv, HttpSession session, HttpServletResponse response,
                             @RequestParam(value = "url",required = false) String url,
                             @RequestParam(value = "title",required = false) String title,
                             @RequestParam(value = "type",required = false) String type){

        //将首次过来的数据保存到session中
        sessionSetString(session,"url",url,true);
        sessionSetString(session,"title",title,true);
        sessionSetString(session,"type",type,false);


        User user = (User) session.getAttribute("user");
        if(user!=null){//说明用户已经存在
            if(!StringUtil.isEmpty(type)){
                mv.setViewName("forward:/fast/open");
            }else if(!StringUtil.isEmpty(url)){
                mv.setViewName("forward:/fast/collection");
            }else {
                mv.setViewName("error");//返回错误页面
            }
        }else{
            mv.setViewName("forward:/");//内部转发到登入请求,去登入页面
        }
        return mv;
    }

    //快速收藏
    @RequestMapping("/collection")
    public ModelAndView collection(ModelAndView mv, HttpSession session){
        mv.setViewName("fastCollection");
        mv.addObject("url",session.getAttribute("url"));
        mv.addObject("label",session.getAttribute("label"));
        session.removeAttribute("url");
        session.removeAttribute("label");
        return mv;
    }

    //快速打开
    @RequestMapping("/open")
    public ModelAndView open(ModelAndView mv, HttpSession session){
        mv.setViewName("fastOpen");
        mv.addObject("type",session.getAttribute("type"));
        session.removeAttribute("type");
        return mv;
    }


    //在session中设置String类型的数据
    public void sessionSetString(HttpSession session, String name, String str, boolean isTrans){
        try {

            if(StringUtil.isEmpty(str)){//如果为空,则不设置值
//                session.setAttribute(name, null);
            }else {
                if(isTrans){
                    session.setAttribute(name, URLDecoder.decode(str,"utf-8"));
                }else {
                    session.setAttribute(name, str);
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


}
