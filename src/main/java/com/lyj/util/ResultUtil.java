package com.lyj.util;

import com.lyj.model.Result;
import com.lyj.service.AlipayService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by 陆英杰
 * 2018/9/27 0:38
 */
public class ResultUtil {

    private static Log logger = LogFactory.getLog(ResultUtil.class);

    public static Result success(String message){
        return success(message,null);
    }
    public static Result success(Object data){
        return new Result(Result.SUCCESS,"成功",data);
    }
    public static Result success(String message,Object data){
        return new Result(Result.SUCCESS,message,data);
    }

    public static Result error(String message){
        return error(message,null);
    }
    public static Result error(Object data){
        logger.error("失败:"+data);
        return new Result(Result.ERROR,"失败",data);
    }
    public static Result error(String message,Object data){
        logger.error(message);
        return new Result(Result.ERROR,message,data);
    }
    public static Result error(String message,Integer code,Object data){
        logger.error(message);
        return new Result(code,message,data);
    }

}
