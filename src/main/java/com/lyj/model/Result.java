package com.lyj.model;

/**
 * Created by 陆英杰
 * 2018/9/27 0:35
 */

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * http请求返回的最外层对象
 */

@NoArgsConstructor(force = true) //生成无参构造方法
@Getter
@Setter
public class Result<T> {

    public static Integer SUCCESS=0;
    public static Integer ERROR=1;

    //错误码
    private Integer code;
    //信息
    private String message;
    //具体内容
    private T data;

    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }


}
