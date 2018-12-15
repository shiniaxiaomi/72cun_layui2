package com.lyj.dao;

import com.lyj.model.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * Created by 陆英杰
 * 2018/9/25 9:42
 */
@Repository
public interface UserDao{

    //add,delete.update,get
    //增
    @Insert("insert into user (password,userName,rootFolderId,customFolderId) values (#{password},#{userName},#{rootFolderId},#{customFolderId})")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id") //数据插入成功后，id值被反填到user对象中，调用getId()就可以获取
    int addUser(User user);

    //删

    //改
    @Update("update user set customFolderId=#{customFolderId},customFolderName=#{customFolderName} where id=#{userId}")
    int updateCustomFolder(@Param("customFolderId")int customFolderId,@Param("customFolderName")String customFolderName,  @Param("userId")Integer userId);

    @Update("update user set rootFolderId=#{rootFolderId} where id=#{userId}")
    int updateRootFolderIdByUserId(@Param("rootFolderId") int rootFolderId, @Param("userId") Integer userId);

    //查
    @Select("select count(1) from user where userName=#{userName}")
    int isExists(String userName);

    @Select("select * from user where userName=#{userName}")
    User getUser(String userName);

    @Select("select customFolderId,customFolderName from user where id=#{userId}")
    User getCustomFolder(Integer userId);
}
