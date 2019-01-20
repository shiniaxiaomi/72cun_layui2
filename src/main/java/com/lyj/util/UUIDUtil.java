package com.lyj.util;

import java.util.UUID;

/**
 * Created by Administrator on 2019/1/20.
 */


/**
 * 主要用来生成token,放在redis中,解决分布式session的问题
 */
public class UUIDUtil {

    public static String uuid(){
        return UUID.randomUUID().toString().replace("-","");
    }

}
