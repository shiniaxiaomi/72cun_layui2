package com.lyj.util;

/**
 * Created by Yingjie.Lu on 2019/2/26.
 */
public class PublicVar {

    public static final String urlScore ="urlScore";//链接的总评分排序集合
    public static final String urlClickNumber ="urlClickNumber";//链接的点击量集合
    public static final String urlGoodNumber ="urlGoodNumber";//链接的点赞量集合
    public static final Double clickValue=1.0;//点击分值
    public static final Double goodValue=3.0;//点赞分值

    public static final int updateTime=1;//1分钟后生效网址的分享状态(0表示不进行缓存)

    public static final String userShareScore="userShareScore";//用户的分享链接数量的排序集合
    public static final String userGoodScore="userGoodScore";//用户的获得点赞个数的排序集合

    public static final Long showNumber=50L;//主页最多显示的信息的条数


    public static String hotUrlData = "hotUrlData_Page";//缓存的热点数据的key
    public static String userGoodData = "userGoodData_Page";//缓存的热点数据的key
    public static String userShareData = "userShareData_Page";//缓存的热点数据的key

//    public static int orderType_year=1;//年卡
//    public static int orderType_quarter=2;//季度卡
//    public static int orderType_month=3;//月卡

}
