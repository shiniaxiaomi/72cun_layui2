package com.lyj.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lyj.dao.FolderDao;
import com.lyj.dao.URLDao;
import com.lyj.exception.MessageException;
import com.lyj.model.Folder;
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
    URLDao urlDao;

//    public void insertDefaultFolder(User user){
//        Folder folder = new Folder(VarUtil.intFalse, "默认文件夹", 0, user.getId(), 0);
//        folderDao.addFolder(folder);
//    }

    public Folder addRootFolder(int userId){
        Folder folder = new Folder("默认文件夹", 0, userId);
        folderDao.addFolder(folder);
        return folder;//这里返回的folder是有id的
    }


    public List<Folder> getFoldersByUserId(Integer userId){
        List<Folder> folders = folderDao.getFoldersByUserId(userId);
        return folders;
    }


    public boolean addFolder(Folder folder) {
        int flag=folderDao.addFolder(folder);//新增folder,并获取到了自增id
        if(flag==1){
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
        }

        if(num1==1){
            return true;
        }else{
            return false;
        }

//        Folder folder2 = folderDao.getFolderById(folderId);
//        if(folder2==null){
//            return ResultUtil.error("文件夹不存在");
//        }
//
//        if(folder2.getFolderNum()>0){
//            return ResultUtil.error("该文件夹下还有文件夹,先删除子文件夹!");
//        }
//
//        //将父文件夹个数减1
//        folderDao.deleteById(folder2.getId());
//        Folder pFloder = folderDao.getFolderById(folder.getPid());
//        if(pFloder==null){
//            return ResultUtil.error("父文件夹不存在");
//        }
//        folderDao.decrFolderNumById(pFloder.getId());
//
//        if(isDefaultFolder){//将自定义文件夹设置成默认文件夹
//            UserSettings settings = userSettingsDao.getUserSettingsByUserId(user.getId());
//            int rootFolderId = folderDao.getFolderIdByUserIdAndPid(user.getId(), 0);//查找根文件夹的id
//            userSettingsDao.updateDefaultFolderId(rootFolderId,user.getId());
//        }
//
//        //将删除文件夹下的所有url删除
//        urlDao.deleteByFolderId(folder.getId());
//
//        return ResultUtil.success("删除成功!");

    }

    @Transactional
    public boolean updateFolder(Folder folder) {

        int i = folderDao.updateFolder(folder);
        if(i==1){
            return true;
        }else{
            return false;
        }

    }

//    public int getRootFolderId(User user) {
//        return folderDao.getFolderIdByUserIdAndPid(user.getId(), 0);//查找根文件夹的id
//    }
//
//    public String getFolderNameById(int id){
//        return folderDao.getFolderNameById(id);
//    }
}
