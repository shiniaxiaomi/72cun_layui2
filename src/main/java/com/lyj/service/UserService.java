package com.lyj.service;

import com.lyj.dao.FolderDao;
import com.lyj.dao.UserDao;
import com.lyj.model.User;
import com.lyj.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Yingjie.Lu on 2018/9/17.
 */

@Service
public class UserService {


    @Autowired
    UserDao userDao;

    @Autowired
    FolderDao folderDao;


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

//    public boolean updateCustomFolderIdByUserId(User user){
//        int i = userDao.updateCustomFolderIdByUserId(user.getCustomFolderId(), user.getId());
//        if(i==1){
//            return true;
//        }else{
//            return false;
//        }
//    }

    public boolean login(User user){
        if(user.getUserName()!=null && user.getPassword()!=null){
            User one = userDao.getUser(user.getUserName());
            if(one!=null && one.getPassword().equals(user.getPassword())){
                user.setId(one.getId());
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
}
