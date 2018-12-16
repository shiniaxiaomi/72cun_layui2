package com.lyj.redisKey;

/**
 * Created by 陆英杰
 * 2018/12/16 22:58
 */
public abstract class BasePrefix implements KeyPrefix{

    private int expireSecond;//过期时间,默认-1代表永不过期

    private String prefix;//前缀

    public BasePrefix(int expireSecond, String prefix) {
        this.expireSecond = expireSecond;
        this.prefix = prefix;
    }

    @Override
    public int expireSecond() {
        return expireSecond;
    }

    @Override
    public String getPrefix() {
        String className = getClass().getSimpleName();
        return className+":"+prefix+":";
    }
}
