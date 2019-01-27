package com.lyj.model;

/**
 * Created by Administrator on 2019/1/21.
 */

import java.util.Date;

/**
 * 公告维护类
 */
public class Notice {

    //记录发布公告的时间
    public static final Long AnnounceTime=1548573246973L;

    //发布公告的js
    public static final String AnnounceJs="" +
            "layer.open({\n" +
            "     type: 1\n" +
            "     ,title: false //不显示标题栏\n" +
            "     ,closeBtn: false\n" +
            "     ,area: '300px;'\n" +
            "     ,shade: 0.8\n" +
            "     ,id: 'LAY_layuipro' //设定一个id，防止重复弹出\n" +
            "     ,resize: false\n" +
            "     ,btn: ['火速围观']\n" +
            "     ,btnAlign: 'c'\n" +
            "     ,moveType: 1 //拖拽模式，0或者1\n" +
            "     ,content:\n" +
            "           //修改公告后需要重新生成发布时间\n" +
            "           `<div style=\"padding: 50px; line-height: 22px; background-color: #393D49; color: #e2e2e2; font-weight: 300; \">\n" +
            "               <h1>新功能上线啦</h1></br></br>\n" +
            "               1.新增手机号注册功能</br></br>\n" +
            "               2.新增找回密码功能</br></br>\n" +
            "               3.支持用户名更改</br></br>\n" +
            "               4.支持绑定手机号和更改手机号</br></br>\n" +
            "               <span style='font-weight:bold'>5.新增用户反馈通道</span></br></br>\n" +
            "               <span style='font-weight:bold'>6.新增网址导入导出功能</span></br></br>\n" +
            "           </div>`\n" +
            "     ,success: function(layero){\n" +
            "     }\n" +
            "});";


    public static void main(String[] args) {
        System.out.println(new Date().getTime());
    }

}
