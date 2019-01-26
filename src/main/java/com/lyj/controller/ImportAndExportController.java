package com.lyj.controller;

import com.lyj.model.Result;
import com.lyj.model.User;
import com.lyj.other.importhtml.ChromeParser;
import com.lyj.other.importhtml.ParseNode;
import com.lyj.service.FolderService;
import com.lyj.service.URLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

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

    @RequestMapping("/importChrome")
    @ResponseBody
    @Transactional
    public Result importChrome(MultipartFile file, User sessionUser){
        ParseNode parseNode=new ChromeParser(folderService,urlService,sessionUser.getRootFolderId(),"默认文件夹",sessionUser.getId());//使用chrome解析类
        Result result = parseNode.start(file);
        return result;
    }







}
