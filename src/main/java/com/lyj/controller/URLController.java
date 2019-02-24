package com.lyj.controller;

import com.github.pagehelper.PageInfo;
import com.lyj.model.Folder;
import com.lyj.model.Result;
import com.lyj.model.URL;
import com.lyj.model.User;
import com.lyj.service.URLService;
import com.lyj.util.PageEntity;
import com.lyj.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

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
    @RequestMapping("/getUrlsByPid")
    public PageEntity<URL> getUrlsByPid(int pid,Integer page, Integer limit,User sessionUser){
        PageInfo<URL> urls = urlService.getUrlsByPid(sessionUser.getId(), pid, page, limit);
        return new PageEntity<>(urls.getTotal(),urls.getList());
    }

    //需要分页
    @RequestMapping("/getUrlsLike")
    public PageEntity<URL> queryAllLike(String keywords, Integer page, Integer limit, Integer searchType,User sessionUser){

        PageInfo<URL> pageInfo=null;
        String[] split = keywords.split("=");

        if(searchType==0){//综合查询
            if(split.length==1){//没有等号,要查询的是 网址名称
                pageInfo= urlService.getUrlsByLabel(sessionUser.getId(), split[0],page,limit);//直接调用 网址名称 查询
            }else if(split.length==2){//有等号
                pageInfo=urlService.getUrlsByLabelAndPidName(sessionUser.getId(),split[0],split[1],page,limit);
            }
        }else if(searchType==1){//按照 网址名称 查询
            pageInfo= urlService.getUrlsByLabel(sessionUser.getId(), keywords,page,limit);;
        }else if(searchType==2){//按照 文件夹名称 查询
            pageInfo= urlService.getUrlsByPidName(sessionUser.getId(),keywords,page,limit);
        }

        return new PageEntity<>(pageInfo.getTotal(),pageInfo.getList());
    }



    @RequestMapping("/update")
    public Result update(URL url){
        if(urlService.updateUrl(url)){
            return ResultUtil.success("更新成功!");
        }else{
            return ResultUtil.error("更新失败!");
        }
    }

    @RequestMapping("/updateInBatches")
    public Result updateInBatches(String id,int pid,String pidName){
        if(urlService.updateUrlsInBatchesByIds(id,pid,pidName)){
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
    @RequestMapping("/deleteInBatches")
    public Result deleteInBatches(String id){
        if(urlService.deleteUrlsInBatchesByIds(id)){
            return ResultUtil.success("删除成功!");
        }else{
            return ResultUtil.error("删除失败!");
        }
    }

    @RequestMapping("/add")
    public Result add(URL url,User sessionUser){
        url.setUserId(sessionUser.getId());
        Date time=new Timestamp(new Date().getTime());//记录生成时间
        url.setCreateTime(time);
        if(url.getIsShare()==true){
            url.setCreateTime(time);
        }

        if(urlService.addUrl(url)){
            return ResultUtil.success("保存成功!",url);
        }else{
            return ResultUtil.error("保存失败!");
        }
    }

    @RequestMapping("/changeShareStatus")
    public Result changeShareStatus(URL url){
        if(url.getIsShare()==true){
            url.setShareTime(new Timestamp(new Date().getTime()));
        }
        urlService.changeShareStatus(url);
        return ResultUtil.success("状态更新成功");
    }



}
