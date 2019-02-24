package com.lyj.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/2/24.
 */

/**
 * 用于对用数据库中hotUrl表，用来记录热点数据，并通过redis进行实时更新展示
 */
@NoArgsConstructor(force = true) //生成无参构造方法
@Getter
@Setter
@Accessors(chain = true)
public class HotUrl implements Serializable {

    private int id;

    private int urlId;//链接的id

    private int clickNumber;//点击量

    private int goodNumber;//点赞量


    public HotUrl(int urlId, int clickNumber, int goodNumber) {
        this.urlId = urlId;
        this.clickNumber = clickNumber;
        this.goodNumber = goodNumber;
    }
}
