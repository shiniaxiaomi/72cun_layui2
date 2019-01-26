package com.lyj.other.exporthtml;

/**
 * Created by Administrator on 2019/1/26.
 */

import com.lyj.exception.MessageException;
import com.lyj.model.Folder;
import com.lyj.model.User;
import com.lyj.service.FolderService;
import com.lyj.service.URLService;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 导出html模板类
 */
public abstract class BuildHtml {

    URLService urlService;

    FolderService folderService;

    User user;

    HttpServletResponse response;

    public BuildHtml(URLService urlService, FolderService folderService, User user, HttpServletResponse response) {
        this.urlService = urlService;
        this.folderService = folderService;
        this.user = user;
        this.response = response;
    }

    //生成html文件并发送给客户端
    public void build(){
        List<Folder> folders = folderService.getFoldersByUserId(user.getId());
        List<Folder> folderTree = bulidTree(folders);//构建文件夹树

        StringBuilder sb=new StringBuilder();

        //构建html(抽象)
        String str=buildHtml(sb,folderTree);

        //向客户端输出html文件
        try {
            String fileName="bookmarks_"+ DateFormat.getDateInstance().format(new Date())+".html";
            // 设置输出的格式
            response.reset();
            response.setContentType("bin");
            response.addHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(str.getBytes());
        } catch (IOException e) {
            throw new MessageException("html生成失败!");
        }
    }

    //构建html抽象方法
    public abstract String buildHtml(StringBuilder sb,List<Folder> folderTree);


    /**
     * 工具方法: 两层循环实现建树
     * 因为每个对象都会持有子节点的引用,所以就可以这样实现(时间复杂度是n²)
     */
    public static List<Folder> bulidTree(List<Folder> folders) {

        List<Folder> folderTree = new ArrayList<>();

        for (Folder folder : folders) {
            if (folder.getPid()==0) {
                folderTree.add(folder);
            }
            for (Folder item : folders) {
                if (folder.getId() == item.getPid()) {//将当前遍历的folder的所有子节点全部添加
                    if (folder.getChildrenList() == null) {
                        folder.setChildrenList(new ArrayList<>());
                    }
                    folder.getChildrenList().add(item);
                }
            }
        }
        return folderTree;
    }



}
