package com.lyj.exception;

/**
 * Created by Yingjie.Lu on 2019/3/19.
 */


/**
 * 订单支付异常
 */
public class AlipayException extends RuntimeException {

    public AlipayException(String message) {
        super(message);
    }

}
