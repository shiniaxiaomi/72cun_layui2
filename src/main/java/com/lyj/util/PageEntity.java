package com.lyj.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Created by 陆英杰
 * 2018/10/17 20:34
 */

//包装分页数据
@NoArgsConstructor(force = true) //生成无参构造方法
@Getter //让lombok自动生成getset方法和无参构造方法
@Setter
public class PageEntity<T> {

    private int code=0;

    private Long count;

    private List<T> data;

    public PageEntity(Long count, List<T> data) {
        this.count = count;
        this.data = data;
    }
}
