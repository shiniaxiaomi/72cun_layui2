package com.lyj.dao;

import org.apache.ibatis.annotations.Delete;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Administrator on 2019/3/2.
 */

@Repository
public interface User_HotUrlDao {

    @Delete("delete from user_hotUrl where likeUrlId=#{urlId}")
    int deleteUser_HotUrlByUrlId(Integer urlId);


    //xml
    int deleteUser_HotUrlByUrlIdBatch(List<Integer> list);
}
