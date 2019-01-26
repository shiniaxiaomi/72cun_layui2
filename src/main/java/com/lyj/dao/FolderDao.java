package com.lyj.dao;

import com.lyj.model.Folder;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by 陆英杰
 * 2018/10/15 14:54
 */
@Repository
public interface FolderDao{

    //add,delete.update,get
    //增
    //增加folder
    @Insert("insert into folder (name,pid,userId) values (#{name},#{pid},#{userId})")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id") //数据插入成功后，id值被反填到folder对象中，调用getId()就可以获取
    int addFolder(Folder f);

    //删
    //删除folder
    @Delete("delete from folder where id=#{id}")
    int deleteByFolderId(Integer id);

    //根据用户id删除所有文件夹
    @Delete("delete from folder where userId=#{userId}")
    int deleteByUserId(Integer userId);

    //改
    //xml
    int updateFolder(Folder folder);


    //查
    //查询该文件夹下还有几个子文件夹
    @Select("select count(1) from folder where userId=#{userId} and pid=#{id}")
    int getChildrenFoldersCountByFolderId(@Param("userId") int userId,@Param("id")Integer id);

    //根据userId和pid获取folder
    @Select("select * from folder where userId=#{userId} and pid=#{pid}")
    Folder getFolderByUserIdAndPid(@Param("userId") Integer userId, @Param("pid") int pid);

    //获取folder
    @Select("select * from folder where id=#{id}")
    Folder getFolderById(Integer id);

    //获取folder集合
    @Select("select * from folder where userId=#{userId}")
    List<Folder> getFoldersByUserId(Integer userId);

    //获取folderId
    @Select("select id from folder where userId=#{userId} and pid=#{pid}")
    int getFolderIdByUserIdAndPid(@Param("userId") Integer userId, @Param("pid") int pid);

    //
    @Select("select name from folder where id=#{id}")
    String getFolderNameByFolderId(int id);

    @Select("select rootFolderId from user where id=#{userId}")
    int getRootFolderIdByUserId(Integer userId);

    @Select("select count(0) from folder where userId=#{userId} and name=#{name}")
    int isExistFolderName(Folder folder);

    @Select("select * from folder where name=#{name} and userId=#{userId}")
    Folder getFolderByFolderNameAndUserId(Folder folder);
}
