package com.lyj.exception;

/**
 * Created by Yingjie.Lu on 2019/3/19.
 */


/**
 * 订单支付异常
 */
public class AlipayException extends RuntimeException {

    private Object data;

    public AlipayException(String message,Object data) {
        super(message);
        this.data=data;
    }

    public Object getData() {
        return data;
    }
}
