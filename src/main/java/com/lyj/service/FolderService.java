package com.lyj.service;

import com.lyj.dao.FolderDao;
import com.lyj.exception.MessageException;
import com.lyj.model.Folder;
import com.lyj.redisKey.FolderKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by 陆英杰
 * 2018/10/15 14:54
 */

@Service
public class FolderService {

    @Autowired
    FolderDao folderDao;


    @Autowired
    URLService urlService;


    @Autowired
    RedisService redisService;






    public Folder addRootFolder(int userId){
        Folder folder = new Folder("默认文件夹", 0, userId);
        folderDao.addFolder(folder);
        return folder;//这里返回的folder是有id的
    }


    public List<Folder> getFoldersByUserId(Integer userId){
        //先查询redis
        List<Folder> list = redisService.getList(FolderKey.getByUserId, userId, Folder.class);
        if(list!=null){
            return list;
        }

        List<Folder> folders = folderDao.getFoldersByUserId(userId);

        //吧查询结果放入redis中
        redisService.set(FolderKey.getByUserId,userId,folders);

        return folders;
    }


    public boolean addFolder(Folder folder) {
        int flag=folderDao.addFolder(folder);//新增folder,并获取到了自增id
        if(flag==1){
            redisService.deleteKey(FolderKey.getByUserId,folder.getUserId());//删除key
            return true;
        }else{
            return false;
        }
    }

    @Transactional
    public boolean deleteFolderByFolderId(int folderId, int userId) {
        int num1=0;
        int count = folderDao.getChildrenFoldersCountByFolderId(userId, folderId);
        if(count>=1){
            throw new MessageException("该文件夹下还有子文件夹,请先删除子文件夹");
        }else{
            num1 = folderDao.deleteByFolderId(folderId);//删除文件夹

            //删除文件夹下的网址
            urlService.deleteUrlByPid(folderId);
        }

        if(num1==1){
            redisService.deleteKey(FolderKey.getByUserId,userId);//删除key
            return true;
        }else{
            return false;
        }


    }

    @Transactional
    public boolean updateFolder(Folder folder) {
        int i = folderDao.updateFolder(folder);
        if(i==1){
            redisService.deleteKey(FolderKey.getByUserId,folder.getUserId());//删除key
            return true;
        }else{
            return false;
        }

    }


}
