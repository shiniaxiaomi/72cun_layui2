package com.lyj.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lyj.dao.URLDao;
import com.lyj.model.URL;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Yingjie.Lu on 2018/9/17.
 */


@Service
public class URLService {


    @Autowired
    URLDao urlDao;


    //1、设置分页信息，包括当前页数和每页显示的总计数
//        PageHelper.startPage(0, 2);
    //2、执行查询
//    List<Folder> folders = folderDao.getFoldersByUserId(userId);
    //3、获取分页查询后的数据
//        PageInfo<Folder> pageInfo = new PageInfo<>(folders);

    public PageInfo<URL> getUrlsByPid(Integer userId, int pid, Integer page, Integer limit) {
        PageHelper.startPage(page, limit);
        List<URL> urls = urlDao.getUrlsByPid(userId, pid);
        return new PageInfo<>(urls);
    }

    //根据label查询url
    public PageInfo<URL> getUrlsByLabel(Integer userId, String label, Integer page, Integer limit) {
        PageHelper.startPage(page, limit);
        List<URL> urls = urlDao.getUrlsByLabel(userId, label);
        return new PageInfo<>(urls);
    }

    //根据label和pidName查询url
    public PageInfo<URL> getUrlsByLabelAndPidName(Integer userId, String label, String pidName,Integer page, Integer limit) {
        PageHelper.startPage(page, limit);
        List<URL> urls = urlDao.getUrlsByLabelAndPidName(userId, label,pidName);
        return new PageInfo<>(urls);
    }

    //根据pidName查询url
    public PageInfo<URL> getUrlsByPidName(Integer userId, String pidName, Integer page, Integer limit) {
        PageHelper.startPage(page, limit);
        List<URL> urls = urlDao.getUrlsByPidName(userId, pidName);
        return new PageInfo<>(urls);
    }

    public boolean updateUrl(URL url) {
        int i = urlDao.updateUrl(url);
        if(i==1){
            return true;
        }else{
            return false;
        }
    }

    public boolean deleteUrl(Integer id) {
        int i = urlDao.deleteUrlByUrlId(id);
        if(i==1){
            return true;
        }else{
            return false;
        }
    }

    public boolean deleteUrlByPid(Integer id) {
        int i = urlDao.deleteUrlByPid(id);
        if(i>=1){
            return true;
        }else{
            return false;
        }
    }

    public boolean addUrl(URL url) {
        int i = urlDao.addUrl(url);
        if(i==1){
            return true;
        }else{
            return false;
        }
    }

    public boolean deleteUrlsInBatchesByIds(String id) {
        String[] split = id.split(",");
        List<Integer> ids=new ArrayList();
        for(int i=0;i<split.length;i++){
            ids.add(Integer.valueOf(split[i]));
        }
        int i = urlDao.deleteUrlsByIds_Batch(ids);
        if(i>=1){
            return true;
        }else{
            return false;
        }
    }

//    public boolean updateUrlsInBatchesByIds(String id, int pid) {
//        String[] split = id.split(",");
//        List<Integer> ids=new ArrayList();
//        for(int i=0;i<split.length;i++){
//            ids.add(Integer.valueOf(split[i]));
//        }
//        Map map=new HashMap();
//        map.put("ids",ids);
//        map.put("pid",pid);
//        int i = urlDao.updateUrlsByIds_Batch(map);
//        if(i>=1){
//            return true;
//        }else{
//            return false;
//        }
//
//    }
    public boolean updateUrlsInBatchesByIds(String id, int pid,String pidName) {
        String[] split = id.split(",");
        List<Integer> ids=new ArrayList();
        for(int i=0;i<split.length;i++){
            ids.add(Integer.valueOf(split[i]));
        }
        int i = urlDao.updateUrlsByIds_Batch(ids,pid,pidName);
        if(i>=1){
            return true;
        }else{
            return false;
        }

    }
}
