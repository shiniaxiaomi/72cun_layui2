package com.lyj.controller;

import com.lyj.model.Folder;
import com.lyj.model.Result;
import com.lyj.model.URL;
import com.lyj.model.User;
import com.lyj.service.FolderService;
import com.lyj.service.URLService;
import com.lyj.service.UserService;
import com.lyj.util.PageEntity;
import com.lyj.util.PublicVar;
import com.lyj.util.ResultUtil;
import com.lyj.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 陆英杰
 * 2018/9/17 0:41
 */

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    URLService urlService;

    @Autowired
    FolderService folderService;



    /**
     * 注册用户
     * @return 返回一个json对象
     */
    @ResponseBody
    @RequestMapping("/add")
    @Transactional  //添加事务
    public Result add(HttpSession session,User user){
        if(!session.getAttribute("code").equals(user.getCode())){
            return ResultUtil.error("验证码输入错误!");
        }else if(!session.getAttribute("phoneNumber").equals(user.getPhoneNumber())){
            return ResultUtil.error("如更换手机号,请重新获取验证码!");
        }

        if(!StringUtil.isEmpty(user.getUserName()) && !StringUtil.isEmpty(user.getPassword())){
            if(!userService.isExists(user)){//判断是否已经存在该用户名
                userService.addUser(user);//添加用户
                Folder folder = folderService.addRootFolder(user.getId());//创建一个默认的文件夹

                //添加默认的收藏网址
                List<URL> urls=new ArrayList<>();
                Date time = new Date();
                //批量添加的时候默认是共享的
                urls.add(new URL("https://www.72cun.cn", "72cun 网址收藏", folder.getId(),"默认文件夹", user.getId(),user.getUserName(), time,true,time));
                urlService.addUrlBatch(urls,user.getUserName());

                if(userService.updateRootFolderIdByUserId(folder.getId(),user.getId())){
                    return ResultUtil.success("注册成功");
                }
            }else{
                return ResultUtil.error("该用户名已存在");
            }
        }

        return ResultUtil.error("注册失败");
    }

    @ResponseBody
    @RequestMapping("/updatePassword")
    public Result updatePassword(HttpSession session,User user){
        //检验验证码
        if(!session.getAttribute("code").equals(user.getCode())){
            return ResultUtil.error("验证码输入错误!");
        }else if(!session.getAttribute("phoneNumber").equals(user.getPhoneNumber())){
            return ResultUtil.error("如更换手机号,请重新获取验证码!");
        }

        if(userService.updatePassword(user)){
            session.removeAttribute("user");//清除用户缓存
            return ResultUtil.success("密码重置成功!");
        }else{
            return ResultUtil.error("密码重置失败!");
        }
    }

    @ResponseBody
    @RequestMapping("/isUserNameExist")
    public Result isUserNameExist(String userName){
        if(userService.isUserNameExist(userName)){
            return ResultUtil.error("用户名已存在!");
        }else{
            return ResultUtil.success("用户名可用");
        }
    }

    @ResponseBody
    @RequestMapping("/isPhoneNumberExist")
    public Result isPhoneNumberExist(User sessionUser){
        if(sessionUser.getPhoneNumber()!=null && !sessionUser.getPhoneNumber().equals("")){
            return ResultUtil.error("该用户已绑定手机,请进行更换手机号操作");
        }else{
            return ResultUtil.success("该用户未绑定手机,可以进行绑定");
        }
    }

    @ResponseBody
    @RequestMapping("/getUserInfo")
    public Result getUserInfo(User sessionUser){
        User user1 = userService.getUserByUserId(sessionUser);
        return ResultUtil.success(user1);
    }

    @ResponseBody
    @RequestMapping("/updateUserName")
    public Result updateUserName(User user,User sessionUser){
        user.setId(sessionUser.getId());
        if(userService.updateUserName(user)){
            return ResultUtil.success("用户名更改成功!");
        }else{
            return ResultUtil.error("用户名更改失败!");
        }
    }

    @ResponseBody
    @RequestMapping("/updatePhoneNumber")
    public Result updatePhoneNumber(User user,User sessionUser,HttpSession session){
        //检验验证码
        if(!session.getAttribute("code").equals(user.getCode())){
            return ResultUtil.error("验证码输入错误!");
        }else if(!session.getAttribute("phoneNumber").equals(user.getPhoneNumber())){
            return ResultUtil.error("如更换手机号,请重新获取验证码!");
        }

        user.setId(sessionUser.getId());
        if(userService.updatePhoneNumber(user)){
            return ResultUtil.success(null);
        }else{
            return ResultUtil.error(null);
        }
    }


    @ResponseBody
    @RequestMapping("/checkPassword")
    public Result checkPassword(User user,User sessionUser){
        user.setId(sessionUser.getId());
        if(userService.checkPassword(user)){
            return ResultUtil.success("密码正确!");
        }else{
            return ResultUtil.error("密码错误!");
        }
    }


    //获取用户的更文件夹
    @RequestMapping("/getRootFolderId")
    @ResponseBody
    public int getRootFolderId(User sessionUser){
        return userService.getRootFolderIdByUserId(sessionUser.getId());
    }


    //获取用户的自定义文件夹
    @ResponseBody
    @RequestMapping("/getCustomFolder")
    public Result getCustomFolder(User sessionUser){

        User user1=userService.getCustomFolder(sessionUser.getId());

        if(user1.getCustomFolderName()!=null){
            return ResultUtil.success(user1);
        }else{
            return ResultUtil.error("你还没有自定文件夹",user1);
        }
    }

    //更改用户的自定义文件夹
    @ResponseBody
    @RequestMapping("/updateCustomFolder")
    public Result updateCustomFolder(User user,User sessionUser){

        user.setId(sessionUser.getId());

        if(userService.updateCustomFolder(user)!=null){
            return ResultUtil.success("自定义文件夹成功");
        }else{
            return ResultUtil.error("自定义文件夹失败");
        }
    }


    //获得用户的分享数量的用户排名
    @ResponseBody
    @RequestMapping("/getShareUserOrder")
    public PageEntity<User> getShareUserOrder(Integer page,int limit){
        int size=limit;
        if(page> PublicVar.showNumber/size){
            return new PageEntity<>(0L, new ArrayList<User>(), page);
        }
        return userService.getShareUserOrder(page, size);
    }

    //获得用户的点按数量的用户排名
    @ResponseBody
    @RequestMapping("/getGoodUserOrder")
    public PageEntity<User> getGoodUserOrder(Integer page,int limit){
        int size=limit;
        if(page> PublicVar.showNumber/size){
            return new PageEntity<>(0L, new ArrayList<User>(), page);
        }
        return userService.getGoodUserOrder(page, size);
    }

    //更新session中的user信息
//    @RequestMapping("/updateSessionUser")
//    public void updateSessionUser(User user,HttpSession session){
//        User updateUser = userService.getUserByUserId(user);
//        session.setAttribute("user",updateUser);
//    }

}
