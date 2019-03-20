package com.lyj.service;

import com.lyj.dao.OrderDao;
import com.lyj.exception.AlipayException;
import com.lyj.exception.MessageException;
import com.lyj.model.Order;
import com.lyj.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

/**
 * Created by Yingjie.Lu on 2019/3/19.
 */

@Service
public class OrderService {

    @Autowired
    OrderDao orderDao;

    @Autowired
    UserService userService;


    //添加支付成功订单，并将用户设置成会员
    public void addOrder(HttpSession session, Order order, User sessionUser) {
        int i = orderDao.addOrder(order);
        if(i!=1) throw new AlipayException(null,order);//退款

        userService.addDeadline(session,order,sessionUser);//给用户开通会员
    }

}
