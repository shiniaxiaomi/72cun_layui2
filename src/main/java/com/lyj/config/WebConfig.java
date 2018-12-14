package com.lyj.config;

import com.lyj.config.interceptor.LoginCheckInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.*;

import java.util.List;

/**
 * Created by Yingjie.Lu on 2018/9/17.
 */


@Configuration
public class WebConfig implements WebMvcConfigurer {



    /**
     * 添加自定义的拦截器(用户登入验证)
     *
     * excludePathPatterns: 是根据实际请求的url路径进行排除的 (就是http://localhost:8087/之后的url路径)
     *      /index/** : 表示以/index/开头的url路径全部都会被拦截
     * addPathPatterns: 是根据实际请求的url路径进行排除的 (就是http://localhost:8087/之后的url路径)
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册自定义拦截器，添加拦截路径和排除拦截路径
        //拦截的作用是判断是否已经登入(即判断session中是否已经有user对象)

        //这个拦截的要看的就是请求的url是否包含指定的内容
        InterceptorRegistration loginCheckInterceptor = registry.addInterceptor(new LoginCheckInterceptor());//添加登入拦截器
        loginCheckInterceptor.addPathPatterns("/**").excludePathPatterns( "/", "/login","/user/add","/exit", "/fast/saveAndLogin");

        loginCheckInterceptor.excludePathPatterns("/css/**","/icon/**","/images/**","/js/**","/layui/**","/myjs/**","/public/**");//排除static下的静态文件


    }

    /**
     * addViewControllers可以方便的实现一个请求直接映射成视图，而无需书写controller
     * registry.addViewController("请求路径").setViewName("请求页面文件路径")
     *
     * 此方法可以很方便的实现一个请求到视图的映射，而无需书写controller
     *
     * 这个ViewControllers的优先级比Interceptors低,所以如果Interceptors没有配置放行,基本上会被拦截掉
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addViewController("/login").setViewName("main");//将/login请求映射到template下的main.html页面

    }

    //配置视图解析器
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
    }


    //配置静态资源路径
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
    }

    //配置请求带过来的参数,如果在这个配置了的话,以后请求就可以在参数中直接使用
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        //添加自定义的ModelAndViewArgumentResolver,这样,之后出现在参数中的ModelAndView都会有一个变量:baseCssAndJs(用来引入css和js)
        //如果静态资源发生改变,只需在ModelAndViewArgumentResolver类中修改静态资源的名字和静态资源本身的名字即可
        //这样实现的效果就是静态资源可以缓存时间可以设置成无穷大
//        resolvers.add(new ModelAndViewArgumentResolver());//添加自定义的ModelAndViewArgumentResolver
    }



}
