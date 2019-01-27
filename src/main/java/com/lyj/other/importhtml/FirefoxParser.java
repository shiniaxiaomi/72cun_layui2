package com.lyj.other.importhtml;

/**
 * Created by Administrator on 2019/1/27.
 */

import com.lyj.model.Folder;
import com.lyj.model.URL;
import com.lyj.service.FolderService;
import com.lyj.service.URLService;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Date;

/**
 * 解析火狐浏览器
 */
public class FirefoxParser extends ParseNode{

    public FirefoxParser(FolderService folderService, URLService urlService, Integer pid, String pidName, Integer userId) {
        super(folderService, urlService, pid, pidName, userId);
    }

    //解析节点
    @Override
    public void parseNode(Elements elements, int pid, String pidName, int userId) {
        for(Element el:elements){
            if(el.tagName().equals("a")){
                //保存url的所有信息,包括父文件夹的id和name,然后再进行批量保存即可
                URL url=new URL();
                url.setUrl(el.attr("href"));
                url.setLabel(el.text());
                url.setPid(pid);
                //谷歌浏览器记录的时间时以秒来算的,换成成毫秒要*1000
                url.setCreateTime(new Date(Long.parseLong(el.attr("add_date"))*1000));
                url.setPidName(pidName);
                url.setUserId(userId);
                list.add(url);
                continue;
            }else if(el.tagName().equals("h3")){
                //保存folder的所有信息,并将属于这个folder节点的element再传入解析函数
                Folder folder=new Folder();
                folder.setName(el.text());
                folder.setPid(pid);
                folder.setUserId(userId);
                if(!folderService.isExistFolderName(folder)){
                    folderService.addFolder(folder);//保存文件夹
                    pid=folder.getId();
                    pidName=folder.getName();
                }else{
                    //查询已存在的文件夹的id,作为传入下一个的pid
                    Folder f = folderService.getFolderByFolderName(folder);
                    pid=f.getId();//将文件夹的id赋值给pid
                    pidName=f.getName();
                }
            }
            parseNode(el.children(),pid,pidName,userId);
        }
    }

    //返回主体内容的节点
    @Override
    public Element getElement(Document doc) {
        Element element = doc.selectFirst("body > dl");//通过css来进行筛选元素
        Elements children = element.children();
        Element element1 = children.get(children.size() - 1);
        return element1.selectFirst("dl");
    }
}
