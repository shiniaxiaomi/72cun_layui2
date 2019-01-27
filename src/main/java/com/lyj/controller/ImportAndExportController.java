package com.lyj.controller;

import com.lyj.model.Folder;
import com.lyj.model.Result;
import com.lyj.model.URL;
import com.lyj.model.User;
import com.lyj.other.exporthtml.*;
import com.lyj.other.importhtml.*;
import com.lyj.service.FolderService;
import com.lyj.service.URLService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2019/1/25.
 */

/**
 * 网址的导入或导出
 */

@Controller
public class ImportAndExportController {

    @Autowired
    FolderService folderService;

    @Autowired
    URLService urlService;

    //谷歌浏览器
    @RequestMapping("/importChrome")
    @ResponseBody
    @Transactional
    public Result importChrome(MultipartFile file, User sessionUser){
        ParseNode parseNode=new ChromeParser(folderService,urlService,sessionUser.getRootFolderId(),"默认文件夹",sessionUser.getId());//使用chrome解析类
        Result result = parseNode.start(file);
        return result;
    }

    @RequestMapping("/exportChrome")
    public void exportChorme(HttpServletResponse response,User sessionUser){
        BuildHtml buildHtml=new ChromeBuildHtml(urlService,folderService,sessionUser,response);
        buildHtml.build();//生成html文件并发送给客户端
    }

    //火狐浏览器
    @RequestMapping("/importFirefox")
    @ResponseBody
    @Transactional
    public Result importFirefox(MultipartFile file, User sessionUser){
        ParseNode parseNode=new FirefoxParser(folderService,urlService,sessionUser.getRootFolderId(),"默认文件夹",sessionUser.getId());//使用chrome解析类
        Result result = parseNode.start(file);
        return result;
    }

    @RequestMapping("/exportFirefox")
    public void exportFirefox(HttpServletResponse response,User sessionUser){
        BuildHtml buildHtml=new FirefoxBuildHtml(urlService,folderService,sessionUser,response);
        buildHtml.build();//生成html文件并发送给客户端
    }

    //IE浏览器
    @RequestMapping("/importIE")
    @ResponseBody
    @Transactional
    public Result importIE(MultipartFile file, User sessionUser){
        ParseNode parseNode=new IEParser(folderService,urlService,sessionUser.getRootFolderId(),"默认文件夹",sessionUser.getId());//使用chrome解析类
        Result result = parseNode.start(file);
        return result;
    }

    @RequestMapping("/exportIE")
    public void importIE(HttpServletResponse response,User sessionUser){
        BuildHtml buildHtml=new IEBuildHtml(urlService,folderService,sessionUser,response);
        buildHtml.build();//生成html文件并发送给客户端
    }


    //360浏览器
    @RequestMapping("/import360")
    @ResponseBody
    @Transactional
    public Result import360(MultipartFile file, User sessionUser){
        ParseNode parseNode=new _360Parser(folderService,urlService,sessionUser.getRootFolderId(),"默认文件夹",sessionUser.getId());//使用chrome解析类
        Result result = parseNode.start(file);
        return result;
    }

    @RequestMapping("/export360")
    public void import360(HttpServletResponse response,User sessionUser){
        BuildHtml buildHtml=new _360BuildHtml(urlService,folderService,sessionUser,response);
        buildHtml.build();//生成html文件并发送给客户端
    }








}
