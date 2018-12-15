package com.lyj.util;

import com.lyj.model.Result;

/**
 * Created by 陆英杰
 * 2018/9/27 0:38
 */
public class ResultUtil {

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
        return new Result(Result.ERROR,"失败",data);
    }
    public static Result error(String message,Object data){
        return new Result(Result.ERROR,message,data);
    }

//    public static Result success(){
//        return success(null,null);
//    }
//
//    //返回成功
//    public static Result success(Object data){
//        return success(null,data);
//    }
//
//    //返回成功
//    public static Result success(String message){
//        return success(message,null);
//    }
//
//    //返回成功
//    public static Result success(String message, Object data){
//        Result result=new Result();
//        result.setCode(200);
//        result.setMessage(message);
//        result.setData(data);
//        result.setState("成功");
//        return result;
//    }
//
//    //返回失败(封装异常)
//    public static Result error(String message, Object data){
//        Result result=new Result();
//        result.setCode(400);
//        result.setMessage(message);
//        result.setData(data);
//        result.setState("失败");
//        return result;
//    }
//
//    //返回成功
//    public static Result error(String message){
//        return error(message,null);
//    }
//
//    //返回失败
//    public static Result error(Object data){
//        return error(null,data);
//    }
//
//    //返回成功
//    public static Result error(){
//        return error(null,null);
//    }

}
