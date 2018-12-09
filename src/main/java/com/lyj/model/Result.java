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

    //错误码
    private Integer code;
    //提示信息
    private String message;
    //具体内容
    private T data;
    //状态
    private String state;

    public Result(Integer code, String message, T data, String state) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.state = state;
    }

}
