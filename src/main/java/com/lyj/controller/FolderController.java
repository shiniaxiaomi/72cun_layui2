package com.lyj.controller;

import com.lyj.model.Folder;
import com.lyj.model.Result;
import com.lyj.model.User;
import com.lyj.service.FolderService;
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
        }

        folderService.deleteFolderByFolderId(folder.getId(),sessionUser.getId(),sessionUser.getUserName());
        return ResultUtil.success("文件夹删除成功");
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

}
