package com.lyj.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 陆英杰
 * 2018/10/15 14:48
 */

@NoArgsConstructor(force = true) //生成无参构造方法
@Getter //让lombok自动生成getset方法和无参构造方法
@Setter
public class Folder implements Serializable {
    private int id;

    private String name;

    private int pid;

    private int userId;

    @JsonIgnore
    private List<Folder> childrenList;//只是在导出html中会用到这个字段

    public Folder(String name, int pid, int userId) {
        this.name = name;
        this.pid = pid;
        this.userId = userId;
    }
}
