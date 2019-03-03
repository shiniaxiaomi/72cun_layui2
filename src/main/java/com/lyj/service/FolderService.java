package com.lyj.service;

import com.lyj.dao.FolderDao;
import com.lyj.exception.MessageException;
import com.lyj.model.Folder;
import com.lyj.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    UserService userService;

    public Folder addRootFolder(int userId){
        Folder folder = new Folder("默认文件夹", 0, userId);
        folderDao.addFolder(folder);
        if(folder==null){
            throw new MessageException("默认文件夹生成失败");
        }
        return folder;//这里返回的folder是有id的
    }


    //如果缓存有,则从缓存中取
    @Cacheable(value = "folder",key = "'folders-userId:'+#userId")
    public List<Folder> getFoldersByUserId(Integer userId){
        return folderDao.getFoldersByUserId(userId);
    }


    //该操作后,清除缓存
    @CacheEvict(value = "folder",key = "'folders-userId:'+#folder.userId")
    public boolean addFolder(Folder folder) {
        int i=folderDao.addFolder(folder);//新增folder,并获取到了自增id
        return i==1 ? true : false;
    }

    //在添加文件夹的时候不更新redis缓存
    public boolean addFolderWithoutCache(Folder folder) {
        int i=folderDao.addFolder(folder);//新增folder,并获取到了自增id
        return i==1 ? true : false;
    }

    //该操作后,清除缓存
    @CacheEvict(value = "folder",key = "'folders-userId:'+#userId")
    @Transactional
    public void deleteFolderByFolderId(int folderId, int userId,String userName) {
        int num1=0;
        int count = folderDao.getChildrenFoldersCountByFolderId(userId, folderId);
        if(count>=1){
            throw new MessageException("该文件夹下还有子文件夹,请先删除子文件夹");
        }else{
            User customFolder = userService.getCustomFolder(userId);
            if(customFolder.getCustomFolderId()==folderId){ throw new MessageException("该文件夹是自定义文件夹,请先更换自定义文件夹后再进行删除!"); }
            num1 = folderDao.deleteByFolderId(folderId);//删除文件夹
            //删除文件夹下的网址
            urlService.deleteUrlByPid(folderId,userName);
        }

        if(num1==0){ throw new MessageException("文件夹删除失败！"); }

    }

    //清除缓存在redis中的folder
    @CacheEvict(value = "folder",key = "'folders-userId:'+#userId")
    public void cleanFolderCache(int userId){

    }

    //该操作后,清除缓存
    @CacheEvict(value = "folder",key = "'folders-userId:'+#folder.userId")
    @Transactional
    public boolean updateFolder(Folder folder) {
        int i = folderDao.updateFolder(folder);
        return i==1 ? true : false;
    }


    public void deleteFolderByUserId(Integer userId) {
        int i = folderDao.deleteByUserId(userId);
        if(i==0) throw new MessageException("用户文件夹删除失败");
    }

    public boolean isExistFolderName(Folder folder){
        return folderDao.isExistFolderName(folder)==1;
    }


    public Folder getFolderByFolderName(Folder folder) {
        return folderDao.getFolderByFolderNameAndUserId(folder);
    }

    public List<Folder> getFoldersByPid(Folder folder) {
        return folderDao.getFoldersByPid(folder);
    }
}
