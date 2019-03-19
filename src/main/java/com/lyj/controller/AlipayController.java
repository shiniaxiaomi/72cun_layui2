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
import com.lyj.service.OrderService;
import com.lyj.util.ResultUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Yingjie.Lu on 2019/3/18.
 */

@RestController
@RequestMapping("/alipay")
public class AlipayController {

    private static Log logger = LogFactory.getLog(AlipayController.class);

    @Autowired
    AlipayTradeService tradeService;

    @Autowired
    OrderService orderService;


    @RequestMapping("/getErweima")
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
                logger.info("支付宝预下单成功: )");
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


    @RequestMapping("/queryOrder")
    public Result queryOrder(Order order,User sessionUser){
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
//                    for (TradeFundBill bill : response.getFundBillList()) {
//                        System.out.println(bill.getFundChannel() + ":" + bill.getAmount());
//                    }
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
                    orderService.addOrder(order,sessionUser);//记录订单，并开通会员
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


    //todo 获取支付宝的异步回调通知（有没有都可以了，因为现在已经有同步的ajax在轮询，但是还有一个就是要设置支付的过期时间，如果支付
    // 时间过期，则立马撤销订单，防止用户继续支付，但是我方系统却没有进行开通会员）
    public void getNotify(){

    }

    // 测试当面付2.0查询订单
    public void test_trade_query(String id) {
        // (必填) 商户订单号，通过此商户订单号查询当面付的交易状态
        String outTradeNo = id;

        // 创建查询请求builder，设置请求参数
        AlipayTradeQueryRequestBuilder builder = new AlipayTradeQueryRequestBuilder().setOutTradeNo(outTradeNo);

        AlipayF2FQueryResult result = tradeService.queryTradeResult(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                logger.info("查询返回该订单支付成功: )");

                AlipayTradeQueryResponse response = result.getResponse();
                System.out.println(response);

                logger.info(response.getTradeStatus());
                if (Utils.isListNotEmpty(response.getFundBillList())) {
                    for (TradeFundBill bill : response.getFundBillList()) {
                        logger.info(bill.getFundChannel() + ":" + bill.getAmount());
                    }
                }
                break;

            case FAILED:
                logger.error("查询返回该订单支付失败或被关闭!!!");
                break;

            case UNKNOWN:
                logger.error("系统异常，订单支付状态未知!!!");
                break;

            default:
                logger.error("不支持的交易状态，交易返回异常!!!");
                break;
        }
    }

    // 测试当面付2.0生成支付二维码
    public void test_trade_precreate() {
        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
//        String outTradeNo = "tradeprecreate" + System.currentTimeMillis()
//                            + (long) (Math.random() * 10000000L);
        String outTradeNo = "tradeprecreate" + 1;

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = "xxx品牌xxx门店当面付扫码消费";

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = "0.01";

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = "购买商品3件共20.00元";

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
        // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
        GoodsDetail goods1 = GoodsDetail.newInstance("goods_id001", "xxx小面包", 1000, 1);
        // 创建好一个商品后添加至商品明细列表
        goodsDetailList.add(goods1);

        // 继续创建并添加第一条商品信息，用户购买的产品为“黑人牙刷”，单价为5.00元，购买了两件
        GoodsDetail goods2 = GoodsDetail.newInstance("goods_id002", "xxx牙刷", 500, 2);
        goodsDetailList.add(goods2);

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl("http://usetools.cn/alipay/tongbu?a=1")//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);

        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                logger.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                System.out.println(response);

                // 需要修改为运行机器上的路径
                String filePath = String.format("/Users/sudo/Desktop/qr-%s.png",
                        response.getOutTradeNo());
                logger.info("filePath:" + filePath);
                //                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, filePath);
                break;

            case FAILED:
                logger.error("支付宝预下单失败!!!");
                break;

            case UNKNOWN:
                logger.error("系统异常，预下单状态未知!!!");
                break;

            default:
                logger.error("不支持的交易状态，交易返回异常!!!");
                break;
        }
    }

}
