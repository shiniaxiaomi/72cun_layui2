package com.lyj.service;

import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.TradeStatus;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradeQueryRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradeRefundRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.model.result.AlipayF2FQueryResult;
import com.alipay.demo.trade.model.result.AlipayF2FRefundResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.utils.Utils;
import com.lyj.controller.AlipayController;
import com.lyj.model.Order;
import com.lyj.model.Result;
import com.lyj.model.User;
import com.lyj.util.ResultUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Yingjie.Lu on 2019/3/20.
 */

@Service
public class AlipayService {

    private static Log logger = LogFactory.getLog(AlipayService.class);

    @Autowired
    AlipayTradeService tradeService;

    @Autowired
    OrderService orderService;

    //获取支付二维码
    public Result getErweima(User sessionUser, Order order){
        //(必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线;
        // 需保证商户系统端不能重复，建议通过数据库sequence生成;
        order.setOrderId(new Date().getTime()+"-"+sessionUser.getId());//下单时间戳-用户id

        // 商品明细列表，用户支付时可以看到具体的付款商品信息
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
        // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
        GoodsDetail goods = GoodsDetail.newInstance(order.getOrderId(), order.getTitle(), Math.round(order.getPrice() * 1000), 1);
        // 创建好一个商品后添加至商品明细列表
        goodsDetailList.add(goods);


        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject("72cun-网址收藏 "+order.getTitle())//(必填) 订单标题，粗略描述用户的支付目的。
                .setTotalAmount(String.valueOf(order.getPrice()))//(必填) 订单总金额，单位为元，不能超过1亿元
                .setOutTradeNo(order.getOrderId())//(必填) 商户网站订单系统中唯一订单号(唯一标识)
                .setGoodsDetailList(goodsDetailList)
                .setStoreId("test_store_id") //(必填) 商户门店编号
//                .setNotifyUrl("http://usetools.cn/alipay/tongbu?a=1")//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setTimeoutExpress("1m")   //120分钟后过期
                ;
//                .setUndiscountableAmount(undiscountableAmount)
//                .setSellerId(sellerId)
//                .setBody(body)
//                .setOperatorId(operatorId)
//                .setExtendParams(extendParams)


        //发起预下单请求
        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                logger.info("支付宝预下单成功");
                System.out.println(result.getResponse());
                order.setPay_url(result.getResponse().getQrCode());//设置支付链接，用于生成支付二维码
                return ResultUtil.success("支付宝预下单成功",order);
            case FAILED:
                return ResultUtil.error("支付宝预下单失败!!!");
            case UNKNOWN:
                return ResultUtil.error("系统异常，预下单状态未知!!!");
            default:
                return ResultUtil.error("不支持的交易状态，交易返回异常!!!");
        }
    }

    //查询支付结果
    public Result queryOrder(HttpSession session,Order order, User sessionUser){
        // 创建查询请求builder，设置请求参数
        if(order.getOrderId()==null || order.getOrderId().equals("")){
            return ResultUtil.error("订单编号不能为空！");
        }
        // 创建查询请求builder，设置请求参数
        AlipayTradeQueryRequestBuilder builder = new AlipayTradeQueryRequestBuilder().setOutTradeNo(order.getOrderId());
        // 发起查询请求
        AlipayF2FQueryResult result = tradeService.queryTradeResult(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                AlipayTradeQueryResponse response = result.getResponse();
                System.out.println(response);
                Double alipayPrice=null;//已经支付的金额
                if (Utils.isListNotEmpty(response.getFundBillList())) {
                    String amount = response.getFundBillList().get(0).getAmount();
                    alipayPrice = Double.valueOf(amount);
                }

                //将前端返回的价格和支付宝返回的已经支付的金额进行对比，如果一致，才会支付成功
                if(alipayPrice==null || order.getPrice()!=alipayPrice){
                    return ResultUtil.error("购买失败：检测到您支付的金额和购买的会员金额不等，属于异常订单，支付金额将会退回到您的账户，请重新购买！");
                }
                order.setUserId(sessionUser.getId());
                order.setUserName(sessionUser.getUserName());
                try {
                    orderService.addOrder(session,order,sessionUser);//记录订单，并开通会员
                }catch (DuplicateKeyException e){
                    logger.error("重复支付："+e);
                }
                return ResultUtil.success("订单支付成功");
            case FAILED:
                return ResultUtil.error("订单支付失败或被关闭!!!");
            case UNKNOWN:
                return ResultUtil.error("系统异常，订单支付状态未知!!!");
            default:
                return ResultUtil.error("不支持的交易状态，交易返回异常!!!");
        }
    }

    //支付宝退款(refundReason:退款原因)
    public Result refund(Order order,String refundReason){
        // 创建退款请求builder，设置请求参数
        AlipayTradeRefundRequestBuilder builder = new AlipayTradeRefundRequestBuilder()
                .setOutTradeNo(order.getOrderId()) // (必填) 外部订单号，需要退款交易的商户外部订单号
                .setRefundAmount(String.valueOf(order.getPrice())) // (必填) 退款金额，该金额必须小于等于订单的支付金额，单位为元
                .setRefundReason(refundReason) // (必填) 退款原因，可以说明用户退款原因，方便为商家后台提供统计
                .setStoreId("test_store_id"); // (必填) 商户门店编号，退款情况下可以为商家后台提供退款权限判定和统计等作用，详询支付宝技术支持

        AlipayF2FRefundResult result = tradeService.tradeRefund(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                return ResultUtil.success("支付宝退款成功");
            case FAILED:
                return ResultUtil.error("支付宝退款失败");
            case UNKNOWN:
                return ResultUtil.error("系统异常，订单退款状态未知!!!");
            default:
                return ResultUtil.error("不支持的交易状态，交易返回异常!!!");
        }
    }



}
