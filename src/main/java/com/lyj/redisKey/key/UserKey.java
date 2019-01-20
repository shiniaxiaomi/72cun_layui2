package com.lyj.redisKey.key;

import com.lyj.redisKey.BasePrefix;

/**
 * Created by 陆英杰
 * 2018/12/16 22:59
 */
public class UserKey extends BasePrefix {

    public UserKey(int expireSecond, String prefix) {
        super(expireSecond, prefix);
    }


    //保存根文件夹id的key
    public static UserKey getRootFolderByUserId =new UserKey(-1,"getRootFolderByUserId");


}
