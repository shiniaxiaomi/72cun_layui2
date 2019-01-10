package com.lyj.model;


import com.fasterxml.jackson.annotation.JsonFormat;
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

    private Integer id;

    private String userName;

    private String password;

    private int rootFolderId;

    private int customFolderId;

    private String customFolderName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date lastLoginTime;//最后一次的登入时间


}
