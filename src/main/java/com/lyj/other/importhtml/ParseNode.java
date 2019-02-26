package com.lyj.other.importhtml;

/**
 * Created by Administrator on 2019/1/26.
 */

import com.lyj.exception.MessageException;
import com.lyj.model.Result;
import com.lyj.model.URL;
import com.lyj.service.FolderService;
import com.lyj.service.URLService;
import com.lyj.util.ResultUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 解析html模板类
 */

public abstract class ParseNode {
    FolderService folderService;

    URLService urlService;

    int pid;
    String pidName;
    int userId;
    List<URL> list=new ArrayList<>();//创建存放url的集合,便于之后批量提交

    public ParseNode(FolderService folderService, URLService urlService, Integer pid, String pidName, Integer userId) {
        this.folderService = folderService;
        this.urlService = urlService;
        this.pid = pid;
        this.pidName = pidName;
        this.userId = userId;
    }

    //模板
    public Result start(MultipartFile file){
        if (!file.isEmpty()) {
            try {
                String html = new String(file.getBytes());
                Document doc = Jsoup.parse(html);//将html解析成doc文档

                //抽象
                Element element = getElement(doc);
                //抽象
                parseNode(element.children(),this.pid,this.pidName,this.userId);//解析文件夹和网址

                //批量保存url
                try{
                    urlService.addUrlBatch(list);
                }catch (DataIntegrityViolationException e){
                    String s = e.getCause().toString();
                    String[] split = s.split("row");
                    int index=Integer.parseInt(split[1].trim())-1;
                    throw new MessageException("标题为<"+list.get(index).getLabel()+">的网址过长,请检查网址后再试:");
                }
            } catch (IOException e) {
                throw new MessageException("IO异常!");
            }
            return ResultUtil.success("上传成功!");
        }else{
            return ResultUtil.error("文件不能为空!");
        }
    }

    //解析节点
    public abstract void parseNode(Elements elements, int pid, String pidName, int userId);

    //获取要解析的主体节点
    public abstract Element getElement(Document doc);

}
