package com.lyj.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;


/**
 * Created by 陆英杰
 * 2018/9/25 11:36
 */

@NoArgsConstructor(force = true) //生成无参构造方法
@Getter
@Setter
public class URL {
    private Integer id;

    private String url;

    private String label;

    private int pid=-1;

    private String pidName;//父文件夹的名称

    private int userId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;//创建时间


}
