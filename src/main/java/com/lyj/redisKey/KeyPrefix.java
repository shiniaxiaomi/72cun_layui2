package com.lyj.redisKey;

/**
 * Created by 陆英杰
 * 2018/12/16 22:57
 */
public interface KeyPrefix {

    public int expireSecond();//过期时间

    public String getPrefix();//前缀


}
