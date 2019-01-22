package com.lyj.controller;

import com.github.pagehelper.PageInfo;
import com.lyj.model.Message;
import com.lyj.model.Result;
import com.lyj.model.User;
import com.lyj.service.MessageService;
import com.lyj.util.PageEntity;
import com.lyj.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * Created by Yingjie.Lu on 2019/1/22.
 */

@Controller
@RequestMapping("/message")
public class MessageController {

    @Autowired
    MessageService messageService;

    @RequestMapping("/send")
    @ResponseBody
    public Result send(@Valid Message message, User sessionUser){

        message.setUserId(sessionUser.getId());
        message.setUserName(sessionUser.getUserName());
        message.setSendTime(new Timestamp(new Date().getTime()));

        messageService.addMessage(message);

        return ResultUtil.success("发送成功!");
    }

    @RequestMapping("/getRootMessage")
    @ResponseBody
    public PageEntity<Message> getRootMessage(User sessionUser, Integer page, Integer limit){

        Message message=new Message();
        message.setRootId(0);//设置获取根节点id为0的都是根节点
        message.setUserId(sessionUser.getId());//设置userId

        PageInfo<Message> pageInfo = messageService.getMessages(message, page, limit);
        return new PageEntity<>(pageInfo.getTotal(),pageInfo.getList());
    }

    @RequestMapping("/getMessageDetail")
    public ModelAndView getMessageDetail(User sessionUser,@PathParam(value = "id") int id){
        Message message=new Message();
        message.setRootId(id);
        message.setUserId(sessionUser.getId());

        List<Message> messages = messageService.getMessagesByRootId(message);

        ModelAndView mv=new ModelAndView("systemMessagesDetail");
        mv.addObject("messages",messages);
        mv.addObject("user",sessionUser);

        return mv;

    }

}
