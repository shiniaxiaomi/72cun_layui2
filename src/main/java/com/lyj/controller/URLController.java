package com.lyj.controller;

import com.lyj.model.Result;
import com.lyj.model.URL;
import com.lyj.model.User;
import com.lyj.service.URLService;
import com.lyj.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by 陆英杰
 * 2018/9/25 11:39
 */

@RestController
@RequestMapping("/url")
public class URLController {

    @Autowired
    URLService urlService;


    //需要分页
//    @RequestMapping("/query")
//    public PageEntity<URL> findByFolderByPage(Folder folder, boolean needCount, Integer pageSize, Integer pageIndex, HttpSession session){
//        User user = (User) session.getAttribute("user");
////
//        int count=-1;
//        //如果需要总数
//        if(needCount){
//            count=urlService.getUrlsCountByUserIdAndFolderId(user.getId(),folder.getId());
//        }
//
//        List<URL> urls = urlService.getUrlsByUserIdAndFolderId(user.getId(),folder.getId(),pageIndex,pageSize);
//
//        PageEntity<URL> pageEntity=new PageEntity<>(urls,count,pageIndex,pageSize);
//
//        return pageEntity;
//    }
//
//    //需要分页
//    @RequestMapping("/queryAllLike")
//    public PageEntity<URL> queryAllLike(@RequestParam("urlName") String keywords, boolean needCount, Integer pageIndex, Integer pageSize, Integer searchType, HttpSession session){
//
//        User user = (User) session.getAttribute("user");
//
//        int count=-1;
//        List<URL> urls=null;
//        String[] split = keywords.split("=");
//        //如果需要总数
//        if(needCount){
//            if(searchType==0){//综合查询
//                if(split.length==1){//没有等号,要查询的是 网址名称
//                    count=urlService.getUrlsCountByKeywords(user.getId(),split[0]);//直接调用 网址名称 查询
//                }else if(split.length==2){//有等号
//                    count=urlService.getUrlsCountByUrlNameAndFolderName(user.getId(),split[0],split[1]);
//                }
//            }else if(searchType==1){//按照 网址名称 查询
//                count=urlService.getUrlsCountByKeywords(user.getId(),keywords);
//            }else if(searchType==2){//按照 文件夹名称 查询
//                count=urlService.getUrlsCountByFolderName(user.getId(),keywords);
//            }
//
//        }
//
//        if(searchType==0){//综合查询
//            if(split.length==1){//没有等号,要查询的是 网址名称
//                urls= urlService.queryByUrlName(user.getId(), split[0],pageIndex,pageSize);//直接调用 网址名称 查询
//            }else if(split.length==2){//有等号
//                urls=urlService.getUrlsByUrlNameAndFolderName(user.getId(),split[0],split[1]);
//            }
//        }else if(searchType==1){//按照 网址名称 查询
//            urls= urlService.queryByUrlName(user.getId(), keywords,pageIndex,pageSize);
//        }else if(searchType==2){//按照 文件夹名称 查询
//            urls= urlService.queryByFolderName(user.getId(),keywords,pageIndex,pageSize);
//        }
//
//        PageEntity<URL> pageEntity=new PageEntity<>(urls,count,pageIndex,pageSize);
//        return pageEntity;
//    }



    @RequestMapping("/update")
    public Result update(URL url){
        if(urlService.updateUrl(url)){
            return ResultUtil.success("更新成功!");
        }else{
            return ResultUtil.error("更新失败!");
        }

    }

    @RequestMapping("/delete")
    public Result delete(Integer id){
        if(urlService.deleteUrl(id)){
            return ResultUtil.success("删除成功!");
        }else{
            return ResultUtil.error("删除失败!");
        }
    }

    @RequestMapping("/save")
    public Result save(URL url, int pid, HttpSession session){
        User user = (User) session.getAttribute("user");

        url.setUserId(user.getId());
        url.setPid(pid);
        url.setCreateTime(new Timestamp(new Date().getTime()));

        if(urlService.addUrl(url)){
            return ResultUtil.success("保存成功!");
        }else{
            return ResultUtil.error("保存失败!");
        }
    }


public static void main(String[] args) {
    String str="";

    String[] split = str.split("=");
    for(int i=0;i<split.length;i++){
        System.out.println(split[i]);
    }
}


}
