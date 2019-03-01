package com.lyj.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by 陆英杰
 * 2018/9/25 9:40
 */

@NoArgsConstructor(force = true) //生成无参构造方法
@Getter //让lombok自动生成getset方法和无参构造方法
@Setter
public class User implements Serializable {

    private int id;

    private String userName;

    @JsonIgnore
    private String password;//在返回json时不返回密码

    private int rootFolderId;

    private int customFolderId;

    private String customFolderName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date lastLoginTime;//最后一次的登入时间

    private String phoneNumber;

    //验证码(不存在数据库中)
    private String code;

    private int shareNumber;//用户的分享数量

    private int goodNumber;//用户的点赞数量


}
