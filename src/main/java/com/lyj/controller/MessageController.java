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

        //如果发送的不是根节点
        //则判断根节点的userId和现在的userId是不是一样的,如果是一样的,则isMark=0,若不一样,则isMark=1
        if(message.getRootId()!=0){
            Message message1 = messageService.getMessageById(message.getRootId());
            if(message1.getUserId()==sessionUser.getId()){//则isMark标记为0
                if(message1.getIsMark()!=0){
                    //标记已发送
                    setSendFlag(message1.getId());
                }
            }else{//则isMark标记为1
                //标记已返回
                setReturnFlag(message1.getId());
            }
        }

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

    @RequestMapping("/delete")
    @ResponseBody
    public Result marked(Message message){
        if(messageService.delete(message)){
            return ResultUtil.success("删除成功!");
        }else{
            return ResultUtil.error("删除失败!");
        }
    }

    @RequestMapping("/getMessageDetail")
    public ModelAndView getMessageDetail(User sessionUser,@PathParam(value = "id") int id){

        Message message=new Message();
        message.setRootId(id);
//        message.setUserId(sessionUser.getId());

        List<Message> messages = messageService.getMessagesByRootId(message);

        ModelAndView mv=new ModelAndView("feedbackMessagesDetail");
        mv.addObject("messages",messages);
        mv.addObject("user",sessionUser);
        mv.addObject("rootId",id);

        return mv;
    }

    @RequestMapping("/getMessageDetailManager")
    public ModelAndView getMessageDetailManager(User sessionUser,@PathParam(value = "id") int id,
                                @PathParam(value = "userId") int userId,@PathParam(value = "userName") String userName){

        Message message=new Message();
        message.setRootId(id);
//        message.setUserId(userId);//这里设置的是这个消息的发起人的id

        List<Message> messages = messageService.getMessagesByRootId(message);

        ModelAndView mv=new ModelAndView("admin/feedbackMessagesDetailManager");
        mv.addObject("messages",messages);
        mv.addObject("user",sessionUser);
        mv.addObject("userName",userName);//消息发起方的用户名
        mv.addObject("rootId",id);

        return mv;
    }

    //标记已读
    @RequestMapping("/markRead")
    @ResponseBody
    public Result markRead(Message message){
        if(messageService.markRead(message)){
            return ResultUtil.success("标记已读成功!");
        }else{
            return ResultUtil.error("标记已读失败!");
        }
    }

    //分页获取所有已经标记为已发送的消息
    @RequestMapping("/getSendedMessages")
    @ResponseBody
    public PageEntity<Message> getSendedMessages(Integer page, Integer limit){
        PageInfo<Message> getSendedMessages = messageService.getSendedMessages(page, limit);
        return new PageEntity<>(getSendedMessages.getTotal(),getSendedMessages.getList());
    }

    @RequestMapping("/setSendFlag")
    @ResponseBody
    public Result setSendFlag(int id){
        Message message=new Message();
        message.setSendTime(new Timestamp(new Date().getTime()));//在标记为未读时会更新发送时间
        message.setId(id);
        message.setIsMark(0);//标记为已发送

        if(messageService.setSendFlag(message)){
            return ResultUtil.success(null);
        }else{
            return ResultUtil.error("标记失败");
        }
    }

    @RequestMapping("/setReturnFlag")
    @ResponseBody
    public Result setReturnFlag(int id){
        Message message=new Message();
        message.setSendTime(new Timestamp(new Date().getTime()));//在标记为未读时会更新发送时间
        message.setId(id);
        message.setIsMark(1);//标记为已发送

        if(messageService.setReturnFlag(message)){
            return ResultUtil.success(null);
        }else{
            return ResultUtil.error("标记失败");
        }
    }

}
