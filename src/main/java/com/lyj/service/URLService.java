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
        return i==1 ? true : false;
    }

    public boolean deleteUrl(Integer id) {
        int i = urlDao.deleteUrlByUrlId(id);
        return i==1 ? true : false;
    }

    public boolean deleteUrlByPid(Integer id) {
        int i = urlDao.deleteUrlByPid(id);
        return i>=1 ? true : false;
    }

    public boolean addUrl(URL url) {
        int i = urlDao.addUrl(url);
        return i==1 ? true : false;
    }

    public boolean deleteUrlsInBatchesByIds(String id) {
        String[] split = id.split(",");
        List<Integer> ids=new ArrayList();
        for(int i=0;i<split.length;i++){
            ids.add(Integer.valueOf(split[i]));
        }
        int i = urlDao.deleteUrlsByIds_Batch(ids);
        return i>=1 ? true : false;
    }

    public boolean updateUrlsInBatchesByIds(String id, int pid,String pidName) {
        String[] split = id.split(",");
        List<Integer> ids=new ArrayList();
        for(int i=0;i<split.length;i++){
            ids.add(Integer.valueOf(split[i]));
        }
        int i = urlDao.updateUrlsByIds_Batch(ids,pid,pidName);
        return i>=1 ? true : false;

    }

    public int deleteUrlByUserId(Integer userId) {
        return urlDao.deleteUrlByUserId(userId);
    }

    public void addUrlBatch(List<URL> list) {
        urlDao.addUrlBatch(list);
    }
}
