package com.lyj.controller;

import com.lyj.model.Folder;
import com.lyj.model.Result;
import com.lyj.model.User;
import com.lyj.redisKey.key.UserKey;
import com.lyj.service.FolderService;
import com.lyj.service.RedisService;
import com.lyj.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by 陆英杰
 * 2018/10/15 14:53
 */

@RestController
@RequestMapping("/folder")
public class FolderController {

    @Autowired
    FolderService folderService;

    @Autowired
    RedisService redisService;


    @RequestMapping("/query")
    public Result<Folder> query(User sessionUser){
        List<Folder> folders = folderService.getFoldersByUserId(sessionUser.getId());

        return ResultUtil.success(folders);

    }

    @RequestMapping("/addFolder")
    public Result addFolder(Folder folder, User sessionUser){

        folder.setUserId(sessionUser.getId());

        if(folderService.addFolder(folder)){
            return ResultUtil.success("添加成功",folder);
        }else{
            return ResultUtil.error("添加失败",folder);
        }
    }

    @RequestMapping("/delete")
    public Result<Folder> delete(Folder folder, User sessionUser){
        if(folder.getPid()==0){
            return ResultUtil.error("根文件夹不能删除");
        }else if(folderService.deleteFolderByFolderId(folder.getId(),sessionUser.getId())){
            return ResultUtil.success("删除成功");
        }else{
            return ResultUtil.error("删除失败");
        }
    }


    @RequestMapping("/update")
    public Result update(Folder folder,User sessionUser){

        folder.setUserId(sessionUser.getId());

         if(folderService.updateFolder(folder)){
             return ResultUtil.success("更新成功");
         }else{
             return ResultUtil.error("更新失败");
         }
    }

    @RequestMapping("/getRootFolderId")
    public int getRootFolderId(User sessionUser){

        Integer rootId = redisService.get(UserKey.getRootFolderByUserId, sessionUser.getId(), Integer.class);//从redis中获取rootFolderId
        if(rootId==null){//如果为空,则从数据库查询,并保存到redis中
            rootId = folderService.getRootFolderIdByUserId(sessionUser.getId());
            redisService.set(UserKey.getRootFolderByUserId, sessionUser.getId(), rootId);
        }

        return rootId;
    }


}
