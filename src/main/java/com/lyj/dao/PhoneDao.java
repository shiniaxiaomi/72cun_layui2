package com.lyj.dao;

import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * Created by 陆英杰
 * 2018/9/25 9:42
 */
@Repository
public interface PhoneDao {


    //add,delete.update,get
    //增


    //删

    //改

    //查
    @Select("select count(*) from user where phoneNumber = #{phoneNumber}")
    int isPhoneNumberExist(String phoneNumber);


}
