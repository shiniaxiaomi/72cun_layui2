package com.lyj.controller;

import com.lyj.exception.MessageException;
import com.lyj.model.Notice;
import com.lyj.model.Result;
import com.lyj.model.User;
import com.lyj.service.UserService;
import com.lyj.util.BASE64Util;
import com.lyj.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

/**
 * Created by 陆英杰
 * 2018/12/8 23:57
 */

@Controller
public class LoginController {

    @Autowired
    Notice notice;//公告类

    @Autowired
    UserService userService;

    @RequestMapping("/")
    public ModelAndView login(HttpSession session, ModelAndView mv){

        mv.setViewName("homePage");//网站主页

        User user = (User) session.getAttribute("user");
        if(user!=null){//说明用户已经存在
            mv.addObject("user",user);
        }

        //从session中获取值并放入mv
        sessionToMV(session,mv,"url");
        sessionToMV(session,mv,"title");
        sessionToMV(session,mv,"type");

        return mv;
    }

    //主页的登入请求
    @RequestMapping("/homeLogin")
    @ResponseBody
    public Result homeLogin(User user, HttpSession session,HttpServletResponse response) throws Exception {
        User sqlUser = userService.login(session,user);
        if(sqlUser==null){
            return ResultUtil.error("用户名或密码错误");
        }

        //根据用户上一次的登入时间和发布公告的时间来判断是否显示公告(如果登入时间为空,则直接显示公告)
        if(sqlUser.getLastLoginTime()==null || sqlUser.getLastLoginTime().getTime()<notice.getNoticeTime()){
            return ResultUtil.success(Notice.AnnounceJs,sqlUser);//将公告通过ajax传回去，并使用js展示出来
        }

        //添加用户的cookie，方便下次登入不需要用户名和密码即可登入
        Cookie cookie=new Cookie("urps", BASE64Util.encryptBASE64(user.getUserName()+","+user.getPassword()));//urps是user和password
        response.addCookie(cookie);

        return ResultUtil.success(null,sqlUser);
    }

    /**
     * forward(转发):
     *      1.表示服务器内部进行的转发,但是浏览器上的网址却没有发生变化
     *      2.是服务器内部的重定向，服务器直接访问目标地址的 url网址，把里面的东西读取出来，但是客户端并不知道，
     *        因此用forward的话，客户端浏览器的网址是不会发生变化的。
     * redirect(重定向):
     *      1.是客户端的重定向，是完全的跳转。即服务器返回的一个url给客户端浏览器，
     *        然后客户端浏览器会重新发送一次请求，到新的url里面，因此浏览器中显示的url网址会发生变化。
     *      2.因为这种方式比forward多了一次网络请求，因此效率会低于forward。
     *
     *  mv.setViewName("forward:/index");//url: http://localhost:8087/index
     *  mv.setViewName("forward:index");//url: http://localhost:8087/user/index    当前路径下的url请求转变
     *  mv.setViewName("forward:/user/index");//url:mv.setViewName("forward:/user/index");
     */
    //其他操作的登入请求
    @RequestMapping("/login")
    public ModelAndView login(User user,HttpSession session, HttpServletResponse response, ModelAndView mv,
                              @RequestParam(value = "url",required = false) String url,
                              @RequestParam(value = "title",required = false) String title,
                              @RequestParam(value = "type",required = false) String type) throws Exception {
        User sqlUser=null;
        User sessionUser = (User) session.getAttribute("user");

        if(sessionUser==null){
            sqlUser =  userService.login(session,user);
        }

        if(sqlUser==null){
            throw new MessageException("用户名或密码错误");
        }

        if(type==null || type.equals("")){
            mv.setViewName("forward:/main");
            //根据用户上一次的登入时间和发布公告的时间来判断是否显示公告(如果登入时间为空,则直接显示公告)
            if(sqlUser.getLastLoginTime()==null || sqlUser.getLastLoginTime().getTime()<notice.getNoticeTime()){
                mv.addObject("showNotice",Notice.AnnounceJs);
            }
        }else if(type.equals("open")){
            mv.setViewName("forward:/fast/open");
        }else if(type.equals("collection")){
            mv.setViewName("forward:/fast/collection");
        }

        //添加用户的cookie，方便下次登入不需要用户名和密码即可登入
        Cookie cookie=new Cookie("urps", BASE64Util.encryptBASE64(user.getUserName()+","+user.getPassword()));//urps是user和password
        response.addCookie(cookie);

        return mv;
    }


    /**
     * 退出登入
     *      使用redirect进行重定向 : 网页进行重定向,直接让客户端重新发起/请求
     */
    @RequestMapping("/exit")
    public String exit(HttpSession session,HttpServletResponse response){
        session.removeAttribute("user");//删除用户
        response.addCookie(new Cookie("urps",""));//清除客户端的用户信息的cookie
        return "redirect:/";
    }


    //从session中获取值并放入mv中,如果为null,则变成""
    public void sessionToMV(HttpSession session, ModelAndView mv, String name){
        Object obj=session.getAttribute(name);
        if(obj==null){//如果为空,则不添加
//            mv.addObject(name,"");
        }else{
            mv.addObject(name,obj);
        }
    }

    @RequestMapping("/getUserFromSession")
    @ResponseBody
    public User getUserFromSession(HttpSession session){
        User user = (User) session.getAttribute("user");
        return user==null?new User():user;
    }


}
