package com.lyj.util;

import java.util.List;

/**
 * Created by 陆英杰
 * 2018/10/17 20:34
 */

//包装分页数据
//@NoArgsConstructor(force = true) //生成无参构造方法
//@Getter //让lombok自动生成getset方法和无参构造方法
//@Setter
public class PageEntity<T> {


    private int totalSize;

    private List<T> content;

    private int pageIndex;

    private int pageSize;

    public PageEntity(List<T> list,int totalSize,int pageIndex,int pageSize) {
        this.totalSize = totalSize;
        this.content = list;
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }

    public PageEntity() {
    }

    public int getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(int totalSize) {
        this.totalSize = totalSize;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
