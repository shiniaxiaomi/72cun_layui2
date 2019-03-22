package com.lyj.config.interceptor;

import com.lyj.model.User;
import com.lyj.service.UserService;
import com.lyj.util.BASE64Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by 陆英杰
 * 2018/9/25 14:02
 */

/**
 * 登入拦截器
 */

@Component
public class LoginCheckInterceptor implements HandlerInterceptor {

    Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    UserService userService;

    /**
     * 在请求前处理,如果返回true,则继续进行拦截器调用,否则,直接退出拦截器,返回对应的结果
     * response.sendRedirect("/index.html");//url: http://localhost:8087/index.html
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //根据session中获取user
        User user = (User) request.getSession().getAttribute("user");

        if(user==null){
            //尝试使用cookie进行登入
            user = userService.tryLogin(request,response);
        }

        //如果还是没有user,则返回登入页面
        if(user==null){
            String requestWith = request.getHeader("X-Requested-With");//获取头信息,用来判断是ajax请求还是页面请求
            if("XMLHttpRequest".equals(requestWith)){//如果是ajax
                if(request.getRequestURI().contains("/getUserFromSession")){
                    return false;
                }else{
                    response.setStatus(309);//设置错误码,然后在客户端进行重定向
                }
            }else{//如果是页面请求

                if(request.getRequestURI().contains("/main")){
                    if(request.getParameterMap().containsKey("home")){
                        response.sendRedirect("/userHome");//从主页过来的
                    }else{
                        response.sendRedirect("/toLogin");//从其他地方过来的，重新请求到登入页面
                    }
                }else if(request.getRequestURI().contains("/saveAndLogin")){
                    response.sendRedirect("/toLogin");//从其他地方过来的，重新请求到登入页面
                }else{
                    response.sendRedirect("/");//重新请求到登入页面
                }

            }

            logger.warn("intercept: "+request.getRequestURL().toString()+" request");
            return false;
        }

        logger.warn("pass: "+request.getRequestURL().toString()+" request");
        return true;
    }

    //对请求进行处理
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    //在请求后处理
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }

}
