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
import com.lyj.util.RedisUtil;
import com.lyj.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    User_HotUrlService user_hotUrlService;

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

    public void deleteUrl(int id,boolean isShare,String userName) {
        int i = urlDao.deleteUrlByUrlId(id);
        if(i==0){
            throw new MessageException("删除失败!");
        }

        //如果这个要删除的网站是分享状态
        if(isShare){
            //先将数据库中的hotUrl表中的数据也删除
            hotUrlService.deleteHotUrlByUrlId(id);
            //再将user_hotUrl表中的关于该网址数据的点赞记录也删除
            int deleteNumber = user_hotUrlService.deleteUser_HotUrlByUrlId(id);

            //将共享数据保存到redis中，保证1点中的时候将redis中的数据同步到数据库中
            redisTemplate.executePipelined(new RedisCallback<String>() {
                @Override
                public String doInRedis(RedisConnection connection) throws DataAccessException {
                    connection.hDel(PublicVar.urlClickNumber.getBytes(), RedisUtil.toByte(id));//删除urlClickNumber中的url
                    connection.hDel(PublicVar.urlGoodNumber.getBytes(), RedisUtil.toByte(id));//删除urlGoodNumber中的url

                    connection.zRem(PublicVar.urlScore.getBytes(),RedisUtil.toByte(id));//删除urlScore排序集合中的数据
                    connection.zIncrBy(PublicVar.userShareScore.getBytes(),-1.0,RedisUtil.toByte(userName));//减少userShareScore用户分享的个数
                    connection.zIncrBy(PublicVar.userGoodScore.getBytes(),-deleteNumber*1.0,RedisUtil.toByte(userName));//减少userGoodScore用户点赞的个数
                    return null;
                }
            });
        }
    }

    public void deleteUrlByPid(Integer id,String userName) {
        List<Integer> ids = urlDao.getShareUrlIdsByPid(id);//获取这个pid下的已经分享的urlId集合
        int i = urlDao.deleteUrlByPid(id);//删除对应的url
        deleteUrlDataBatchByIdsUtil(redisTemplate,ids,userName);//调用工具类批量清除数据和缓存
    }

    public void addUrl(URL url,User sessionUser) {
        int i = urlDao.addUrl(url);
        if(i==0){
            throw new MessageException("网址添加失败!");
        }

        if(url.getIsShare()){
            //将共享数据保存到redis中，保证1点中的时候将redis中的数据同步到数据库中
            redisTemplate.executePipelined(new RedisCallback<String>() {
                @Override
                public String doInRedis(RedisConnection connection) throws DataAccessException {
                    connection.zIncrBy(PublicVar.urlScore.getBytes(),0.0,RedisUtil.toByte(url.getId()));//urlScore中添加记录
                    connection.zIncrBy(PublicVar.userShareScore.getBytes(),1.0,sessionUser.getUserName().getBytes());//增加userShareScore用户分享的个数
                    return null;
                }
            });
        }
    }

    public void deleteUrlsInBatchesByIds(String id,String isShare,String userName) {
        String[] split = id.split(",");
        String[] split_1 = isShare.split(",");
        List<Integer> ids=new ArrayList();
        List<Integer> isShareIds=new ArrayList();
        for(int i=0;i<split.length;i++){
            ids.add(Integer.valueOf(split[i]));
            if(split_1[i].equals("true")){
                isShareIds.add(Integer.valueOf(split[i]));
            }
        }
        if(ids.size()>0){
            int i = urlDao.deleteUrlsByIds_Batch(ids);
            if(i==0){
                throw new MessageException("批量删除失败!");
            }
            //只删除已经分享的数据缓存
            if(isShareIds.size()>0){
                deleteUrlDataBatchByIdsUtil(redisTemplate,isShareIds,userName);//调用工具类批量清除数据和缓存
            }
        }
    }

    public boolean updateUrlsInBatchesByIds(String id, int pid,String pidName) {
        String[] split = id.split(",");
        List<Integer> ids=new ArrayList();
        for(int i=0;i<split.length;i++){
            ids.add(Integer.valueOf(split[i]));
        }

        int i=0;
        if(ids.size()>0){
            i = urlDao.updateUrlsByIds_Batch(ids,pid,pidName);
        }
        return i>=1 ? true : false;

    }

    public void deleteUrlByUserId(Integer userId,String userName) {
        List<Integer> ids = urlDao.getShareUrlIdsByUserId(userId);
        int i = urlDao.deleteUrlByPid(userId);//删除对应的url
        if(i==0) throw new MessageException("用户的链接删除失败");

        deleteUrlDataBatchByIdsUtil(redisTemplate,ids,userName);//调用工具类批量清除数据和缓存
    }

    public int addUrlBatch(List<URL> list,String userName) {
        if(list.size()>0){
            int i= urlDao.addUrlBatch(list);
            if(i==0) throw new MessageException("批量添加失败,添加数为0");
            addUrlDataInUrlScoreBatchByIdsUtil(redisTemplate,list,userName);//在redis中添加缓存
            return i;
        }else{
            return 0;
        }
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
            int deleteNumber = user_hotUrlService.deleteUser_HotUrlByUrlId(url.getId());

            //将共享数据保存到redis中，保证1点中的时候将redis中的数据同步到数据库中
            redisTemplate.executePipelined(new RedisCallback<String>() {
                @Override
                public String doInRedis(RedisConnection connection) throws DataAccessException {
                    connection.hDel(PublicVar.urlClickNumber.getBytes(), RedisUtil.toByte(url.getId()));//删除urlClickNumber中的url
                    connection.hDel(PublicVar.urlGoodNumber.getBytes(), RedisUtil.toByte(url.getId()));//删除urlGoodNumber中的url

                    connection.zRem(PublicVar.urlScore.getBytes(),RedisUtil.toByte(url.getId()));//删除urlScore排序集合中的数据
                    connection.zIncrBy(PublicVar.userShareScore.getBytes(),-1.0,sessionUser.getUserName().getBytes());//减少userShareScore用户分享的个数
                    connection.zIncrBy(PublicVar.userGoodScore.getBytes(),-deleteNumber*1.0,sessionUser.getUserName().getBytes());//减少v用户点赞的个数
                    return null;
                }
            });
        }else{//如果选择共享
            //将共享数据保存到redis中，保证1点中的时候将redis中的数据同步到数据库中
            redisTemplate.executePipelined(new RedisCallback<String>() {
                @Override
                public String doInRedis(RedisConnection connection) throws DataAccessException {
                    connection.zIncrBy(PublicVar.urlScore.getBytes(),0.0,RedisUtil.toByte(url.getId()));//urlScore添加记录
                    connection.zIncrBy(PublicVar.userShareScore.getBytes(),1.0,sessionUser.getUserName().getBytes());//增加userShareScore用户分享的个数
                    return null;
                }
            });
        }

    }

    public List<URL> getUrlsByIdBatch(List list) {
        if(list.size()>0){
            return urlDao.getUrlsByIdBatch(list);
        }else{
            return new ArrayList<>();
        }
    }


    //批量删除网址后清除数据库和缓存的工具
    public void deleteUrlDataBatchByIdsUtil(RedisTemplate redisTemplate,List<Integer> ids,String userName){
        int deleteNumber = hotUrlService.deleteHotUrlByUrlIdBatch(ids);
        int goodNumber = user_hotUrlService.deleteUser_HotUrlByUrlIdBatch(ids);//删除并返回这个网址所点赞的个数

        redisTemplate.executePipelined(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                connection.hDel(PublicVar.urlClickNumber.getBytes(), RedisUtil.toByteArray(ids));//删除urlClickNumber中的url
                connection.hDel(PublicVar.urlGoodNumber.getBytes(), RedisUtil.toByteArray(ids));//删除urlGoodNumber中的url

                connection.zRem(PublicVar.urlScore.getBytes(),RedisUtil.toByteArray(ids));//删除urlScore排序集合中的数据
                connection.zIncrBy(PublicVar.userShareScore.getBytes(),-ids.size()*1.0,RedisUtil.toByte(userName));//减少userShareScore用户分享的个数
                connection.zIncrBy(PublicVar.userGoodScore.getBytes(), -goodNumber*1.0,RedisUtil.toByte(userName));//减少userGoodScore用户点赞的个数
                return null;
            }
        });
    }

    //批量添加网址分享记录在urlScore中
    public void addUrlDataInUrlScoreBatchByIdsUtil(RedisTemplate redisTemplate,List<URL> ids,String userName){
        redisTemplate.executePipelined(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                for(int i=0;i<ids.size();i++){
                    connection.zIncrBy(PublicVar.urlScore.getBytes(),0.0, RedisUtil.toByte(ids.get(i).getId()));//在urlScore中添加记录
                }
                connection.zIncrBy(PublicVar.userShareScore.getBytes(),ids.size()*1.0,RedisUtil.toByte(userName));//在userShareScore中更新用户分享的个数
                return null;
            }
        });
    }

}
