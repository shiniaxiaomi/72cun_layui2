package com.lyj.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lyj.dao.MessageDao;
import com.lyj.model.Message;
import com.lyj.model.Result;
import com.lyj.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Yingjie.Lu on 2019/1/22.
 */

@Service
public class MessageService {

    @Autowired
    MessageDao messageDao;

    public boolean addMessage(Message message) {
        int i = messageDao.addMessage(message);
        return i==1 ? true : false;
    }

    //分页查询消息
    public PageInfo<Message> getMessages(Message message, Integer page, Integer limit) {
        PageHelper.startPage(page, limit);
        List<Message> messages = messageDao.getMessages(message);
        return new PageInfo<>(messages);
    }

    //获取所有用户未读信息的总数
    public int getMessagesCount(User sessionUser) {
        Message message=new Message();
        message.setRootId(0);//设置获取根节点id为0的都是根节点
        message.setUserId(sessionUser.getId());//设置userId
        return messageDao.getMessagesCount(message);
    }

    //查询详细信息
    public List<Message> getMessagesByRootId(Message message) {
        return messageDao.getMessagesByRootId(message);
    }

    public boolean markRead(Message message) {
        int i = messageDao.markRead(message.getId());
        return i==1?true:false;
    }

    public boolean delete(Message message) {
        messageDao.deleteByRootId(message.getId());
        int i = messageDao.deleteById(message.getId());
        return i==1?true:false;
    }

    //分页获取所有未读信息
    public PageInfo<Message> getSendedMessages(Integer page, Integer limit) {
        PageHelper.startPage(page, limit);
        List<Message> messages = messageDao.getSendedMessages();
        return new PageInfo<>(messages);
    }

    //获取所有管理员未读信息的总数
    public int getSendedMessagesCount() {
        return messageDao.getSendedMessagesCount();
    }

    //标记已发送
    public boolean setSendFlag(Message message) {
        int i = messageDao.setIsMark(message);
        return i==1?true:false;
    }

    //标记已返回
    public boolean setReturnFlag(Message message) {
        int i = messageDao.setIsMark(message);
        return i==1?true:false;
    }

    public Message getMessageById(int id) {
       return messageDao.getMessageById(id);
    }

}
