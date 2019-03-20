package com.lyj.handle;

import com.lyj.exception.AlipayException;
import com.lyj.exception.MessageException;
import com.lyj.model.Order;
import com.lyj.model.Result;
import com.lyj.service.AlipayService;
import com.lyj.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;


/**
 * Created by 陆英杰
 * 2018/9/27 0:50
 */

/**
 * 配置异常处理类,处理restful的返回的数据异常
 */
@RestControllerAdvice
@ResponseBody
public class ExceptionHandle {

    @Autowired
    AlipayService alipayService;

//    private final static Logger logger= LoggerFactory.getLogger(ExceptionHandle.class);

    /**
     * 统一的Exception异常处理,可以直接将异常返回给客户端,方便直接观察异常信息
     */
    @ExceptionHandler(value = Exception.class)
    public Result handle(Exception e){
//        logger.error("Exception异常:"+ Arrays.asList(e.getStackTrace()));

        //处理返回异常消息
        if(e instanceof BindException){//jsr303校验异常(数据绑定异常)
            BindException ex = (BindException)e;
            ObjectError error = ex.getAllErrors().get(0);/*取第一个异常*/
            String errorMsg = error.getDefaultMessage(); /*获取异常信息*/
            return ResultUtil.error(errorMsg,error);//返回最上层[0]的错误信息
        }else if(e instanceof MessageException){//自定义消息异常
            return ResultUtil.error(e.getMessage(),e.getStackTrace()[0]);//返回最上层[0]的错误信息
        }else if(e instanceof AlipayException){//支付宝订单支付异常
            //出现异常后，先进行支付宝退款，然后再返回通知消息
            Order order = (Order) ((AlipayException) e).getData();//获取order订单
            Result result = alipayService.refund(order, e.getMessage()==null?"无":e.getMessage());//进行退款
            if(result.getCode()==0){
                return ResultUtil.error("订单异常，支付金额将退回到支付账户，请稍后再试，给您带来不便，实属抱歉！",Result.REFUND_ERROR,null);//返回最上层[0]的错误信息
            }else{
                return ResultUtil.error("订单异常，支付金额将退款失败，请联系管理员进行退款:806648324(qq),给您带来不便，实属抱歉！",Result.REFUND_ERROR,null);//退款失败
            }
        } else{//其他异常
            e.printStackTrace();
            return ResultUtil.error("系统异常",e);
        }
    }
}
