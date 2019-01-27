package com.lyj.other.exporthtml;

/**
 * Created by Administrator on 2019/1/26.
 */

import com.lyj.model.Folder;
import com.lyj.model.URL;
import com.lyj.model.User;
import com.lyj.service.FolderService;
import com.lyj.service.URLService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 导出火狐浏览器的html
 */
public class FirefoxBuildHtml extends BuildHtml{

    public FirefoxBuildHtml(URLService urlService, FolderService folderService, User user, HttpServletResponse response) {
        super(urlService, folderService, user, response);
    }

    //创建外部的html框架
    @Override
    public String buildHtml(StringBuilder sb,List<Folder> folderTree) {

        sb.append("<!DOCTYPE NETSCAPE-Bookmark-file-1>\n" +
                "<!-- This is an automatically generated file.\n" +
                "     It will be read and overwritten.\n" +
                "     DO NOT EDIT! -->\n" +
                "<META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=UTF-8\">\n" +
                "<TITLE>Bookmarks</TITLE>\n" +
                "<H1>书签菜单</H1>\n");

        sb.append("<DL><p>\n");
//        sb.append("<DT><H3 ADD_DATE=\"1548562917\" LAST_MODIFIED=\"1548563145\" PERSONAL_TOOLBAR_FOLDER=\"true\">书签工具栏</H3>\n");

        //创建内部循环的html
        buildFolderAndUrl(sb,folderTree);

        sb.append("</DL><p>\n");

        return sb.toString();
    }

    //创建内部循环的html
    public void buildFolderAndUrl(StringBuilder sb, List<Folder> folderTree) {
        for(Folder folder:folderTree){
            //添加根文件夹(即默认文件夹)
            sb.append("<DT><H3 ADD_DATE=\"1545845245\" LAST_MODIFIED=\"1548434080\">"+folder.getName()+"</H3>\n");

            sb.append("<DL><p>\n");//集合开始
            //================================================
            //先加该文件夹下的所有文件夹文件夹
            if(folder.getChildrenList()!=null){
                buildFolderAndUrl(sb,folder.getChildrenList());
            }

            //再添加属于该文件夹下的url
            List<URL> urls = urlService.getUrlsByFolderId(folder);
            for(URL url:urls){
                sb.append("<DT><A HREF=\""+url.getUrl()+"\" ADD_DATE=\""+url.getCreateTime().getTime()/1000+"\">"+url.getLabel()+"</A>\n");
            }

            //================================================
            sb.append("</DL><p>\n");//集合结束
        }
    }
}
