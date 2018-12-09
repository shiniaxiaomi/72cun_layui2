package com.lyj.service;

import com.lyj.dao.URLDao;
import com.lyj.model.URL;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Created by Yingjie.Lu on 2018/9/17.
 */


@Service
public class URLService {


    @Autowired
    URLDao urlDao;



    public List<URL> getUrlsByPid(Integer userId, int pid, Integer pageIndex, Integer pageSize) {
        RowBounds rowBounds=new RowBounds((pageIndex-1)*pageSize,pageSize);//分页用

        return urlDao.getUrlsByPid(userId,pid,rowBounds);
    }

    public int getUrlsCountByPid(Integer userId, int pid) {
        return urlDao.getUrlsCountByPid(userId,pid);
    }

    public List<URL> getUrlsByLabel(Integer userId, String keywords, Integer pageIndex, Integer pageSize) {
        RowBounds rowBounds=new RowBounds((pageIndex-1)*pageSize,pageSize);//分页用
        return urlDao.getUrlsByKeywords(userId,keywords,rowBounds);
    }

    public int getUrlsCountByKeywords(Integer userId, String keywords) {
        return urlDao.getUrlsCountByKeywords(userId,keywords);
    }

    public List<URL> getUrlsByPidName(Integer userId, String keywords, Integer pageIndex, Integer pageSize) {
        RowBounds rowBounds=new RowBounds((pageIndex-1)*pageSize,pageSize);//分页用
        return urlDao.getUrlsByPidName(userId,keywords,rowBounds);
    }

    public int getUrlsCountByLable(Integer userId, String keywords) {
        return urlDao.getUrlsCountByKeywords(userId,keywords);
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

    public boolean addUrl(URL url) {
        int i = urlDao.addUrl(url);
        if(i==1){
            return true;
        }else{
            return false;
        }
    }

    //综合查询--个数
    public int getUrlsCountByLabelAndPidName(Integer userId, String label, String pidName) {
        return urlDao.getUrlsCountByLabelAndPidName(userId,label,pidName);
    }
    //综合查询-url
    public List<URL> getUrlsByLabelAndPidName(Integer userId, String lable, String pidName) {
        return urlDao.getUrlsByLabelAndPidName(userId,lable,pidName);
    }
}
