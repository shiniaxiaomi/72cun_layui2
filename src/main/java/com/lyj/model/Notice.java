package com.lyj.model;

/**
 * Created by Administrator on 2019/1/21.
 */

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 公告维护类
 */
@NoArgsConstructor(force = true) //生成无参构造方法
@Getter
@Setter//get和set必须要

@Component
@PropertySource("classpath:Notice.properties")
@ConfigurationProperties(prefix = "notice")
public class Notice {

    public Long NoticeTime=0L;

    public String NoticeStringTime;//公告更新字符串时间,从配置文件中获取

    public List NoticeMessage;//要发布的消息

    //发布公告的js
    public static String AnnounceJs="" +
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
            "               #noticeMessage#" +
            "           </div>`\n" +
            "     ,success: function(layero){\n" +
            "     }\n" +
            "});";


    //只执行一次,将字符串时间转化成毫秒值
    @PostConstruct
    public void init() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            NoticeTime=sdf.parse(NoticeStringTime).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //循环生成配置文件中的公告
        String buff="";
        for(int i=0;i<NoticeMessage.size();i++){
            buff+=NoticeMessage.get(i)+"</br></br>\n";
        }
        AnnounceJs=AnnounceJs.replace("#noticeMessage#",buff);

    }


//    public static void main(String[] args) {
//        System.out.println(new Date().getTime());
//    }

}
