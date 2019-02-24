package com.lyj.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lyj.dao.URLDao;
import com.lyj.exception.MessageException;
import com.lyj.model.Folder;
import com.lyj.model.Result;
import com.lyj.model.URL;
import com.lyj.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Yingjie.Lu on 2018/9/17.
 */


@Service
public class URLService {

    @Autowired
    URLDao urlDao;

    public PageInfo<URL> getUrlsByPid(Integer userId, int pid, Integer page, Integer limit) {
        PageHelper.startPage(page, limit);
        List<URL> urls = urlDao.getUrlsByPidPage(userId, pid);
        return new PageInfo<>(urls);
    }


    public List<URL> getUrlsByFolderId(Folder folder) {
        return urlDao.getUrlsByFolderId(folder);
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

    //根据userId和时间查询url
    public PageInfo<URL> getShareUrlsByUserIdLike(String keywords,Integer userId, Integer page, Integer limit) {
        PageHelper.startPage(page, limit);
        List<URL> urls = urlDao.getShareUrlsByUserIdLike(keywords,userId);
        return new PageInfo<>(urls);
    }

    //根据keywords和时间查询所有已经分享的url
    public PageInfo<URL> getShareUrlsLike(String keywords, Integer page, int limit) {
        PageHelper.startPage(page, limit);
        List<URL> urls = urlDao.getShareUrlsLike(keywords);
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

    public Result getUrlsByUserId(int userId) {
        List<URL> urls = urlDao.getUrlsByUserId(userId);
        return ResultUtil.success(urls);
    }

    public void changeShareStatus(URL url) {
        int i = urlDao.changeShareStatus(url);
        if(i!=1){//失败
            throw new MessageException("状态更新失败，请稍后再试！");
        }
    }


}
