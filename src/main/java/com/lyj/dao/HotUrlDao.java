package com.lyj.dao;

import com.lyj.model.HotUrl;
import com.lyj.model.URL;
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
    @Insert("insert into hotUrl (urlId,clickNumber,goodNumber,score) values (#{urlId},#{clickNumber},#{goodNumber},#{score})")
    int addHotUrl(HotUrl hotUrl);

    //标记用户点赞过的链接
    @Insert("insert into user_hotUrl (userId,likeUrlId) values (#{userId},#{likeUrlId})")
    int markIsIncredGoodNumber(User_HotUrl user_hotUrl);

    //删
    //删除用户

    //改
    @Update("UPDATE hotUrl SET clickNumber=clickNumber+1,score=score+1 WHERE urlId=#{urlId}")//浏览量分值为1
    int incrClickNumber(int urlId);

    @Update("UPDATE hotUrl SET goodNumber=goodNumber+1,score=score+3 WHERE urlId=#{urlId}")//收藏量分值为3
    int incrGoodNumber(int urlId);


    //查
    @Select("select count(*) from user_hotUrl where userId=#{userId} and likeUrlId=#{likeUrlId}")
    int isIncredGoodNumber(User_HotUrl user_hotUrl);

    @Select("select url.*,user.userName,hotUrl.clickNumber,hotUrl.goodNumber from hotUrl " +
            "left join url on hotUrl.urlId=url.id " +
            "left join user on user.id=url.userId order by score desc")
    List<URL> getHotUrlByHot();

}
