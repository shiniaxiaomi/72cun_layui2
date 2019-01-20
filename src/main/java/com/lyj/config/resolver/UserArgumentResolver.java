package com.lyj.config.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Administrator on 2019/1/20.
 */

@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    //如果是在requestMapping的方法参数的名称是sessionUser时,则返回true,然后才可以进行下一步的resolveArgument的操作
    //下面的resolveArgument就是针对方法参数的名称是sessionUser的一个处理: 从session中拿到user对象,并设置到方法参数的sessionUser中,方便方法内部使用
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        //参数名称的判断
        String parameterName = methodParameter.getParameterName();
        return parameterName.equals("sessionUser");

        //参数类型的判断
//        Class<?> aClass = methodParameter.getParameterType();
//        return aClass== SessionUser.class;

    }

    //将从session中获取到的user对象设置到方法参数中,方便直接使用
    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {

        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
//        HttpServletResponse response = nativeWebRequest.getNativeResponse(HttpServletResponse.class);

        return request.getSession().getAttribute("user");
    }
}
