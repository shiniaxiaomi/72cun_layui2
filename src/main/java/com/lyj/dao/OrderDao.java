package com.lyj.dao;

import com.lyj.model.Order;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * Created by Yingjie.Lu on 2019/3/19.
 */

@Repository
public interface OrderDao {


    @Insert("insert into `order` (orderId,title,type,payTime,userId,userName,price) " +
            " values (#{orderId},#{title},#{type},now(),#{userId},#{userName},#{price})")
    public int addOrder(Order order);

    @Select("select count(*) from order where orderId=#{orderId}")
    void isExist(Order order);
}
