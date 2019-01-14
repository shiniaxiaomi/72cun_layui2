package com.lyj.controller;

import com.lyj.exception.MessageException;
import com.lyj.model.User;
import com.lyj.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

/**
 * Created by 陆英杰
 * 2018/12/8 23:57
 */

@Controller
public class LoginController {

    @Autowired
    UserService userService;

    @RequestMapping("/")
    public ModelAndView login(HttpSession session, ModelAndView mv){

        mv.setViewName("index");

        User user = (User) session.getAttribute("user");
        if(user!=null){//说明用户已经存在
            mv.addObject("user",user);
            mv.setViewName("forward:/main");
        }

        //从session中获取值并放入mv
        sessionToMV(session,mv,"url");
        sessionToMV(session,mv,"title");
        sessionToMV(session,mv,"type");

        return mv;
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
    @RequestMapping("/login")
    public ModelAndView login(User user, HttpSession session, ModelAndView mv){

        User sessionUser = (User) session.getAttribute("user");

        boolean seccessFlag=false;//标记是否登入成功

        if(sessionUser!=null){//用户已经存在
            seccessFlag=true;
        }else{
            seccessFlag = userService.login(user);
            if(seccessFlag){
                session.setAttribute("user",user);
            }
        }

        //根据不同的参数选择要返回的页面
        if(seccessFlag){
            Object type = session.getAttribute("type");
            Object url = session.getAttribute("url");
            if(type!=null && !"".equals((String)type)){
                mv.setViewName("forward:/fast/open");
            }else if(url!=null && !"".equals((String)url)){
                mv.setViewName("forward:/fast/collection");
            }else{
                mv.setViewName("forward:/main");
            }
        }else {
            throw new MessageException("用户名或密码错误");
        }

        return mv;
    }

    /**
     * 直接返回String类型,然后模板引擎会在返回的字符串后面加上.html后缀,
     * 然后再到templates文件夹中找到对应的模板进行渲染,染回返回给客户端
     *
     * @param  : 返回给模板引擎,在渲染的时候可以直接取到model中设置的值
     */
    @RequestMapping("/main")
    public ModelAndView userMain(ModelAndView mv, HttpSession session){

        mv.setViewName("main");
        mv.addObject("user",session.getAttribute("user"));
        return mv;
    }

    /**
     * 退出登入
     *      使用redirect进行重定向 : 网页进行重定向,直接让客户端重新发起/请求
     */
    @RequestMapping("/exit")
    public String exit(HttpSession session){
        session.removeAttribute("user");//删除用户
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


}
