package com.lyj.service;

import com.lyj.dao.UserDao;
import com.lyj.model.Result;
import com.lyj.model.User;
import com.lyj.util.ResultUtil;
import com.lyj.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by Yingjie.Lu on 2018/9/17.
 */

@Service
public class UserService {


    @Autowired
    UserDao userDao;

    @Autowired
    FolderService folderService;

    @Autowired
    URLService urlService;

    @Autowired
    RedisService redisService;


    public boolean isExists(User user){
        if(!StringUtil.isEmpty(user.getUserName())){
            int num= userDao.isExists(user.getUserName());
            if(num==1){
                return true;
            }
        }
        return false;
    }

    public boolean addUser(User user){
        int i = userDao.addUser(user);
        if(i==1){
            return true;
        }else{
            return false;
        }
    }

    public boolean updateRootFolderIdByUserId(int rootFolderId,int userId){
        int i = userDao.updateRootFolderIdByUserId(rootFolderId, userId);
        if(i==1){
            return true;
        }else{
            return false;
        }
    }


    /**
     * 支持用户名登入和手机号登入
     */
    public boolean login(User user){
        if(user.getUserName()!=null && user.getPassword()!=null){
            User one = userDao.getUserByUserName(user.getUserName());//先使用用户名查询
            if(one==null){
                one = userDao.getUserByPhoneNumber(user.getUserName());//如果查不到用户,则是使用手机号登入
            }

            if(one.getPassword().equals(user.getPassword())){
                user.setId(one.getId());
                user.setUserName(one.getUserName());//重新更新用户名
                //记录用户的登入时间
                userDao.updateLastLoginTime(new Timestamp(new Date().getTime()),one.getId());
                return true;
            }
        }
        return false;
    }


    public boolean updateCustomFolder(int customFolderId,String customFolderName, Integer userId) {
        int i = userDao.updateCustomFolder(customFolderId,customFolderName,userId);
        if(i==1){
            return true;
        }else{
            return false;
        }

    }


    public User getCustomFolder(Integer userId) {
        return userDao.getCustomFolder(userId);
    }

    //删除用户
    @Transactional
    public Result deleteUserById(Integer userId) {
        int deleteUrlCount=urlService.deleteUrlByUserId(userId);
        int deleteFolderCount= folderService.deleteFolderByUserId(userId);
        int deleteUserCount=userDao.deleteById(userId);
        if(deleteFolderCount>0 && deleteUserCount>0){
            return ResultUtil.success("删除成功!");
        }else{
            return ResultUtil.error("删除失败!");
        }
    }

    public boolean updatePassword(User user) {
        int i = userDao.updatePassword(user);
        if(i==1){
            return true;
        }else{
            return false;
        }

    }
}
