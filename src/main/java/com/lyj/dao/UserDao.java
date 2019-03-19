package com.lyj.dao;

import com.lyj.model.Folder;
import com.lyj.model.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by 陆英杰
 * 2018/9/25 9:42
 */
@Repository
public interface UserDao{

    //add,delete.update,get
    //增
    @Insert("insert into user (password,userName,rootFolderId,customFolderId,phoneNumber) values (#{password},#{userName},#{rootFolderId},#{customFolderId},#{phoneNumber})")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id") //数据插入成功后，id值被反填到user对象中，调用getId()就可以获取
    int addUser(User user);

    //删
    //删除用户
    @Delete("delete from user where id=#{userId}")
    int deleteById(Integer userId);

    //改
    @Update("update user set customFolderId=#{customFolderId},customFolderName=#{customFolderName} where id=#{id}")
    int updateCustomFolder(User user);

    @Update("update user set rootFolderId=#{rootFolderId} where id=#{userId}")
    int updateRootFolderIdByUserId(@Param("rootFolderId") int rootFolderId, @Param("userId") Integer userId);

    @Update("update user set lastLoginTime=#{date} where id=#{userId}")
    void updateLastLoginTime(@Param("date")Date date,@Param("userId") Integer userId);

    @Update("update user set password=#{password} where phoneNumber=#{phoneNumber}")
    int updatePassword(User user);

    @Update("update user set userName=#{userName} where id=#{id}")
    int updateUserName(User user);

    @Update("update user set phoneNumber=#{phoneNumber} where id=#{id}")
    int updatePhoneNumber(User user);

    //查
    @Select("select count(*) from user where userName=#{userName}")
    int isUserNameExist(String userName);

    @Select("select * from user where userName=#{userName}")
    User getUserByUserName(String userName);

    @Select("select * from user where phoneNumber=#{phoneNumber}")
    User getUserByPhoneNumber(String phoneNumber);

    @Select("select customFolderId,customFolderName from user where id=#{userId}")
    User getCustomFolder(Integer userId);

    @Select("select * from user where id=#{id}")
    User getUserByUserId(User user);

    @Select("select count(*) from user where id=#{id} and password=#{password}")
    int checkPassword(User user);

    @Select("select rootFolderId from user where id=#{userId}")
    int getRootFolderIdByUserId(Integer userId);

    //xml
    int updateUserShareNumberByUserNameBatch(List<User> list);

    //xml
    int updateUserGoodNumberByUserNameBatch(List<User> list);

    @Update("update user set deadline=DATE_ADD(deadline,INTERVAL #{months} MONTH),isMembership=#{isMembership} where id=#{userId}")
    int addDeadline(@Param("isMembership")boolean isMembership, @Param("months")int months, @Param("userId")int userId);

    @Update("update user set deadline=DATE_ADD(now(),INTERVAL #{months} MONTH),isMembership=#{isMembership} where id=#{userId}")
    int createDeadline(@Param("isMembership")boolean isMembership, @Param("months")int months, @Param("userId")int userId);
}
