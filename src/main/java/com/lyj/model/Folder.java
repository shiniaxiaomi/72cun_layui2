package com.lyj.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by 陆英杰
 * 2018/10/15 14:48
 */

@NoArgsConstructor(force = true) //生成无参构造方法
@Getter //让lombok自动生成getset方法和无参构造方法
@Setter
public class Folder {
    private int id;

    private String name;

    private int pid;

    private int userId;

    public Folder(String name, int pid, int userId) {
        this.name = name;
        this.pid = pid;
        this.userId = userId;
    }
}
