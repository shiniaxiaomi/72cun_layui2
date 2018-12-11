package com.lyj.util;

import com.lyj.exception.MessageException;
import com.lyj.model.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * Created by 陆英杰
 * 2018/9/27 0:50
 */

/**
 * 配置异常处理类,处理restful的返回的数据异常
 */
@RestControllerAdvice
public class ExceptionHandle {

//    private final static Logger logger= LoggerFactory.getLogger(ExceptionHandle.class);

    /**
     * 统一的Exception异常处理,可以直接将异常返回给客户端,方便直接观察异常信息
     */
    @ExceptionHandler(value = Exception.class)
    public Result handle(Exception e){
//        logger.error("Exception异常:"+ Arrays.asList(e.getStackTrace()));

        //处理返回异常消息
        if(e instanceof MessageException){
            System.out.println(e.getMessage());
            return ResultUtil.error(e.getMessage(),e.getStackTrace()[0]);//返回最上层[0]的错误信息
        }else{
            e.printStackTrace();
            return ResultUtil.error("系统异常",e);
        }
    }
}
