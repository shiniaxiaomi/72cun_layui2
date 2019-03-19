package com.lyj.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Yingjie.Lu on 2019/3/19.
 */

/**
 * 支付订单类
 */
@NoArgsConstructor(force = true) //生成无参构造方法
@Getter //让lombok自动生成getset方法和无参构造方法
@Setter
public class Order implements Serializable {

    private int id;
    private String orderId;
    private String title;//前端获取
    private int type;//订单类型
    private Date payTime;
    private int userId;
    private String userName;
    private double price;//前端获取

    //不属于数据库字段
    private String pay_url;

}


