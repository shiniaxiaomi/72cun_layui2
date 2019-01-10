package com.lyj.dao;

import com.lyj.model.User;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by 陆英杰
 * 2018/9/25 9:42
 */
@Repository
public interface AdminDao {


    //add,delete.update,get
    //增

    //删

    //改

    //查
    @Select("select * from user order by lastLoginTime desc")
    List<User> getUsers();

}
