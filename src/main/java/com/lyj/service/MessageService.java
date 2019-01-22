package com.lyj.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lyj.dao.MessageDao;
import com.lyj.model.Message;
import com.lyj.model.Result;
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

    public PageInfo<Message> getMessages(Message message, Integer page, Integer limit) {
        PageHelper.startPage(page, limit);
        List<Message> messages = messageDao.getMessages(message);
        return new PageInfo<>(messages);
    }

    public List<Message> getMessagesByRootId(Message message) {
        return messageDao.getMessagesByRootId(message);
    }

}
