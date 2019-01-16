package com.lyj.redisKey;

/**
 * Created by 陆英杰
 * 2018/12/16 22:59
 */
public class UserRootFolderKey extends BasePrefix {

    public UserRootFolderKey(int expireSecond, String prefix) {
        super(expireSecond, prefix);
    }


    public static UserRootFolderKey getRootFolderByUserId =new UserRootFolderKey(-1,"userId");


}
