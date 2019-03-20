package com.lyj.controller;

import com.alipay.api.domain.TradeFundBill;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradeQueryRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.model.result.AlipayF2FQueryResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.utils.Utils;
import com.lyj.model.Order;
import com.lyj.model.Result;
import com.lyj.model.User;
import com.lyj.service.AlipayService;
import com.lyj.service.OrderService;
import com.lyj.util.ResultUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Yingjie.Lu on 2019/3/18.
 */

@RestController
@RequestMapping("/alipay")
public class AlipayController {

    @Autowired
    AlipayService alipayService;




    @RequestMapping("/getErweima")
    public Result getErweima(User sessionUser, Order order){
        return alipayService.getErweima(sessionUser,order);
    }


    @RequestMapping("/queryOrder")
    public Result queryOrder(HttpSession session,Order order, User sessionUser){
        return alipayService.queryOrder(session,order,sessionUser);
    }


    //todo 获取支付宝的异步回调通知（有没有都可以了，因为现在已经有同步的ajax在轮询，但是还有一个就是要设置支付的过期时间，如果支付
    // 时间过期，则立马撤销订单，防止用户继续支付，但是我方系统却没有进行开通会员）
    public void getNotify(){

    }

    @RequestMapping("/refund")
    public Result refund(Order order){
        Result refund = alipayService.refund(order, "测试");
        return refund;
    }


}
