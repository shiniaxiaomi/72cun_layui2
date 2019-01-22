package com.lyj.dao;

import com.lyj.model.Message;
import com.lyj.model.Result;
import com.lyj.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by 陆英杰
 * 2018/9/25 9:42
 */
@Repository
public interface MessageDao {



    //add,delete.update,get
    //增
    @Insert("insert into message (detail,userId,sendTime,rootId,parentId,userName) values (#{detail},#{userId},#{sendTime},#{rootId},#{parentId},#{userName})")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id") //数据插入成功后，id值被反填到user对象中，调用getId()就可以获取
    int addMessage(Message message);

    //删

    //改

    //查
    @Select("select * from message where 1=1 and userId=#{userId} and parentId=#{parentId} and rootId=#{rootId}")
    List<Message> getMessages(Message message);

    @Select("select * from message where 1=1 and userId=#{userId} and rootId=#{rootId} order by sendTime")
    List<Message> getMessagesByRootId(Message message);


    List<Message> getMessageByRootId(int id);


}
