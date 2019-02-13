package com.lyj.dao;

import com.lyj.model.Message;
import com.lyj.model.Result;
import com.lyj.model.User;
import org.apache.ibatis.annotations.*;
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
    @Insert("insert into message (detail,userId,sendTime,rootId,isMark,userName) values (#{detail},#{userId},#{sendTime},#{rootId},#{isMark},#{userName})")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id") //数据插入成功后，id值被反填到user对象中，调用getId()就可以获取
    int addMessage(Message message);

    //删
    @Delete("delete from message where id=#{id}")
    int deleteById(int id);

    @Delete("delete from message where rootId=#{rootId}")
    int deleteByRootId(int rootId);

    //改
    @Update("update message set isMark=#{isMark},sendTime=#{sendTime} where id=#{id}")
    int setIsMark(Message message);

    @Update("update message set isMark=-1 where id=#{id}")
    int markRead(int id);

    //查
    @Select("select * from message where userId=#{userId} and rootId=#{rootId} order by isMark desc,sendTime desc")
    List<Message> getMessages(Message message);

    //获取用户未处理的消息总数
    @Select("select count(0) from message where userId=#{userId} and rootId=#{rootId} and isMark=1")
    int getMessagesCount(Message message);

    @Select("select * from message where rootId=#{rootId} order by sendTime")
    List<Message> getMessagesByRootId(Message message);

    @Select("select * from message where rootId=0 and (isMark=0 or isMark=1) order by isMark asc,sendTime desc")
    List<Message> getSendedMessages();

    //获取管理者未处理的消息总数
    @Select("select count(*) from message where rootId=0 and isMark=0")
    int getSendedMessagesCount();

    @Select("select * from message where id=#{id}")
    Message getMessageById(int id);



}
