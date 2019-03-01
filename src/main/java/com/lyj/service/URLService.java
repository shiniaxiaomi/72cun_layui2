package com.lyj.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lyj.dao.URLDao;
import com.lyj.exception.MessageException;
import com.lyj.model.Folder;
import com.lyj.model.Result;
import com.lyj.model.URL;
import com.lyj.model.User;
import com.lyj.util.PublicVar;
import com.lyj.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Yingjie.Lu on 2018/9/17.
 */


@Service
public class URLService {

    @Autowired
    URLDao urlDao;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    HotUrlService hotUrlService;

    //在批量插入时使用（如果label相同有存在，则认为是重复的网址）
    public boolean isExistesUrl(URL url){
        return urlDao.isExistesUrl(url)==0?false:true;
    }

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

    public int addUrlBatch(List<URL> list) {
        return urlDao.addUrlBatch(list);
    }

    public Result getUrlsByUserId(int userId) {
        List<URL> urls = urlDao.getUrlsByUserId(userId);
        return ResultUtil.success(urls);
    }

    @Transactional
    public void changeShareStatus(URL url,User sessionUser) {
        int i = urlDao.changeShareStatus(url);
        if(i!=1){//失败
            throw new MessageException("状态更新失败，请稍后再试！");
        }

        if(url.getIsShare()==false){//如果选择私有
            //先将数据库中的hotUrl表中的数据也删除
            hotUrlService.deleteHotUrlByUrlId(url.getId());
            //再将user_hotUrl表中的关于该网址数据的点赞记录也删除
            hotUrlService.deleteUser_HotUrlByUrlId(url.getId());

            //将共享数据保存到redis中，保证1点中的时候将redis中的数据同步到数据库中
            redisTemplate.executePipelined(new RedisCallback<String>() {
                @Override
                public String doInRedis(RedisConnection connection) throws DataAccessException {
                    connection.hDel(String.valueOf(url.getId()).getBytes());//删除缓存的共享的url数据
                    connection.zRem(PublicVar.hotUrlScore.getBytes(),String.valueOf(url.getId()).getBytes());//删除掉redis中进行排序的数据
                    try {
                        //将中文以utf-8的格式进行编码
                        connection.zIncrBy(PublicVar.userShareScore.getBytes(),-1L,sessionUser.getUserName().getBytes("utf-8"));//减少redis中记录用户分享的个数
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            });
//            Boolean delete = redisTemplate.delete(String.valueOf(url.getId()));//删除缓存的共享的url数据
//            Long num=redisTemplate.opsForZSet().remove(PublicVar.hotUrlScore,url.getId());//删除掉redis中进行排序的数据
//            redisTemplate.opsForZSet().incrementScore(PublicVar.userShareScore,sessionUser.getUserName(),-1L);//减少redis中记录用户分享的个数
        }else{//如果选择共享
            //将共享数据保存到redis中，保证1点中的时候将redis中的数据同步到数据库中
            redisTemplate.executePipelined(new RedisCallback<String>() {
                @Override
                public String doInRedis(RedisConnection connection) throws DataAccessException {
                    connection.hSetNX(String.valueOf(url.getId()).getBytes(), "url".getBytes(), JSON.toJSONString(url).getBytes());//保存url信息
                    connection.zIncrBy(PublicVar.hotUrlScore.getBytes(),0,String.valueOf(url.getId()).getBytes());//在分数表中创建记录
                    connection.zIncrBy(PublicVar.userShareScore.getBytes(),1,sessionUser.getUserName().getBytes());//增加redis中记录用户分享的个数
                    return null;
                }
            });
        }

    }

    public List<URL> getUrlsByIdBatch(List list) {
        return urlDao.getUrlsByIdBatch(list);
    }

}
