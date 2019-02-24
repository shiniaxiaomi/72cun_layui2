package com.lyj.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;


/**
 * Created by 陆英杰
 * 2018/9/25 11:36
 */

@NoArgsConstructor(force = true) //生成无参构造方法
@Getter
@Setter
@Accessors(chain = true)
public class URL implements Serializable {
    private Integer id;

    private String url;

    private String label;

    private int pid=-1;

    private String pidName;//父文件夹的名称

    private int userId;

    private String userName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;//创建时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date shareTime;//链接共享的时间

    private Boolean isShare=true;//标记该网址是否分享(默认分享)

    //不属于数据库字段
    private int clickNumber;//点击量

    //不属于数据库字段
    private int goodNumber;//点赞量

    public URL(String url, String label, int pid, String pidName, int userId, Date createTime) {
        this.url = url;
        this.label = label;
        this.pid = pid;
        this.pidName = pidName;
        this.userId = userId;
        this.createTime = createTime;
    }

    public URL(String url, String label, int pid, String pidName, int userId, Date createTime,boolean isShare) {
        this.url = url;
        this.label = label;
        this.pid = pid;
        this.pidName = pidName;
        this.userId = userId;
        this.createTime = createTime;
        this.isShare=isShare;
    }

    public URL(String url, String label, int pid, String pidName, int userId,String userName, Date createTime,boolean isShare) {
        this.url = url;
        this.label = label;
        this.pid = pid;
        this.pidName = pidName;
        this.userId = userId;
        this.userName=userName;
        this.createTime = createTime;
        this.isShare=isShare;
    }

    public URL(String url, String label, int pid, String pidName, int userId,String userName, Date createTime,boolean isShare,Date shareTime) {
        this.url = url;
        this.label = label;
        this.pid = pid;
        this.pidName = pidName;
        this.userId = userId;
        this.userName=userName;
        this.createTime = createTime;
        this.isShare=isShare;
        this.shareTime=shareTime;
    }
}
