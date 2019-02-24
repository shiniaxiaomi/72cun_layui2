package com.lyj.dao;

import com.lyj.model.HotUrl;
import com.lyj.model.linkModel.User_HotUrl;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by 陆英杰
 * 2018/9/25 9:42
 */
@Repository
public interface HotUrlDao {


    //add,delete.update,get
    //增
    @Insert("insert into hotUrl (urlId,clickNumber,goodNumber) values (#{urlId},#{clickNumber},#{goodNumber})")
    int addHotUrl(HotUrl hotUrl);

    //标记用户点赞过的链接
    @Insert("insert into user_hotUrl (userId,likeUrlId) values (#{userId},#{likeUrlId})")
    int markIsIncredGoodNumber(User_HotUrl user_hotUrl);

    //删
    //删除用户

    //改
    @Update("UPDATE hotUrl SET clickNumber=clickNumber+1 WHERE urlId=#{urlId}")
    int incrClickNumber(int urlId);

    @Update("UPDATE hotUrl SET goodNumber=goodNumber+1 WHERE urlId=#{urlId}")
    int incrGoodNumber(int urlId);


    //查
    @Select("select count(*) from user_hotUrl where userId=#{userId} and likeUrlId=#{likeUrlId}")
    int isIncredGoodNumber(User_HotUrl user_hotUrl);

}
