package com.lyj.exception;

/**
 * Created by 陆英杰
 * 2018/12/9 16:16
 */

/**
 * 包装错误消息的异常类
 */
public class MessageException extends RuntimeException{

    public MessageException(String message) {
        super(message);
    }
}
