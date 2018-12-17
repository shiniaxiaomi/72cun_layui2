package com.lyj.controller;

import com.lyj.model.Folder;
import com.lyj.model.Result;
import com.lyj.model.User;
import com.lyj.service.FolderService;
import com.lyj.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
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
    public Result<Folder> query(HttpSession session){
        User user = (User) session.getAttribute("user");
        List<Folder> folders = folderService.getFoldersByUserId(user.getId());

        return ResultUtil.success(folders);

    }

    @RequestMapping("/addFolder")
    public Result addFolder(Folder folder, HttpSession session){

        User user = (User) session.getAttribute("user");

        folder.setUserId(user.getId());

        if(folderService.addFolder(folder)){
            return ResultUtil.success("添加成功",folder);
        }else{
            return ResultUtil.error("添加失败",folder);
        }
    }

    @RequestMapping("/delete")
    public Result<Folder> delete(Folder folder, HttpSession session){
        User user = (User) session.getAttribute("user");
        if(folder.getPid()==0){
            return ResultUtil.error("根文件夹不能删除");
        }else if(folderService.deleteFolderByFolderId(folder.getId(),user.getId())){
            return ResultUtil.success("删除成功");
        }else{
            return ResultUtil.error("删除失败");
        }
    }


    @RequestMapping("/update")
    public Result update(Folder folder,HttpSession session){
        User user = (User) session.getAttribute("user");

        folder.setUserId(user.getId());

         if(folderService.updateFolder(folder)){
             return ResultUtil.success("更新成功");
         }else{
             return ResultUtil.error("更新失败");
         }
    }

//    @RequestMapping("/getRootFolderId")
//    public int getRootFolderId(HttpSession session){
//        User user = (User) session.getAttribute("user");
//        return folderService.getRootFolderId(user);
//    }


}
