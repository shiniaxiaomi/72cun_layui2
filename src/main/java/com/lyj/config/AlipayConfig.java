package com.lyj.config;

import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeWithHBServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by Yingjie.Lu on 2019/3/18.
 */


@Configuration
public class AlipayConfig {

    // 支付宝当面付2.0服务（集成了交易保障接口逻辑）
    @Bean
    public AlipayTradeService alipayTradeService(){
        /**
         * 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
                */
        Configs.init("zfbinfo.properties");

        // 支付宝当面付2.0服务（集成了交易保障接口逻辑）
       return new AlipayTradeWithHBServiceImpl.ClientBuilder().build();

    }




}
