package com.lyj.redisKey.key;

import com.lyj.redisKey.BasePrefix;

/**
 * Created by 陆英杰
 * 2018/12/16 22:59
 */
public class FolderKey extends BasePrefix {

    public FolderKey(int expireSecond, String prefix) {
        super(expireSecond, prefix);
    }


    public static FolderKey getByUserId =new FolderKey(-1,"getByUserId");


}
