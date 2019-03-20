package com.lyj.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lyj.dao.UserDao;
import com.lyj.exception.AlipayException;
import com.lyj.exception.MessageException;
import com.lyj.model.Order;
import com.lyj.model.Result;
import com.lyj.model.User;
import com.lyj.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;


/**
 * Created by Yingjie.Lu on 2018/9/17.
 */

@Service
public class UserService {


    @Autowired
    UserDao userDao;

    @Autowired
    FolderService folderService;

    @Autowired
    URLService urlService;

    @Autowired
    RedisTemplate redisTemplate;

    public boolean isExists(User user){
        if(!StringUtil.isEmpty(user.getUserName())){
            int num= userDao.isUserNameExist(user.getUserName());
            if(num==1){
                return true;
            }
        }
        return false;
    }

    public boolean addUser(User user){
        int i = userDao.addUser(user);
        if(i==1){
            return true;
        }else{
            throw new MessageException("用户添加失败!");
        }
    }

    public boolean updateRootFolderIdByUserId(int rootFolderId,int userId){
        int i = userDao.updateRootFolderIdByUserId(rootFolderId, userId);
        return i==1 ? true : false;
    }


    /**
     * 支持用户名登入和手机号登入
     */
    public User login(HttpSession session, User user){

        if(user.getUserName()==null || user.getPassword()==null){
            return null;
        }

        User one = userDao.getUserByUserName(user.getUserName());//先使用用户名查询
        if(one==null){
            one = userDao.getUserByPhoneNumber(user.getUserName());//如果查不到用户,则是使用手机号登入
            if(one==null) return null;
        }

        if(one.getPassword().equals(user.getPassword())){
            //记录用户的登入时间
            userDao.updateLastLoginTime(new Timestamp(new Date().getTime()),one.getId());
        }

        session.setAttribute("user",one);//更新session

        return one;
    }


    @CachePut(value = "user",key = "'customFolder-userId:'+#user.id")
    public User updateCustomFolder(User user) {
        int i = userDao.updateCustomFolder(user);
        return i==1 ? user : null;
    }


    @Cacheable(value = "user",key = "'customFolder-userId:'+#userId")
    public User getCustomFolder(Integer userId) {
        return userDao.getCustomFolder(userId);
    }

    //删除用户
    @Transactional
    public Result deleteUserById(Integer userId,String userName) {
        int i = userDao.deleteById(userId);//删除用户
        if(i==0) throw new MessageException("用户删除失败");

        folderService.deleteFolderByUserId(userId);//删除user的文件夹
        urlService.deleteUrlByUserId(userId,userName);//删除user的网址
        return ResultUtil.success("用户删除成功!");
    }

    public boolean updatePassword(User user) {
        int i = userDao.updatePassword(user);
        return i==1 ? true : false;
    }

    public boolean isUserNameExist(String userName) {
        int i = userDao.isUserNameExist(userName);
        return i==1 ? true : false;
    }

    public User getUserByUserId(User user) {
       return userDao.getUserByUserId(user);
    }

    public boolean updateUserName(User user) {
        int i = userDao.updateUserName(user);
        return i==1 ? true : false;
    }

    public boolean updatePhoneNumber(User user) {
        int i = userDao.updatePhoneNumber(user);
        return i==1 ? true : false;
    }

    public boolean checkPassword(User user) {
        int i = userDao.checkPassword(user);
        return i==1 ? true : false;
    }

    @Cacheable(value = "user",key = "'rootFolder-userId:'+#userId")
    public int getRootFolderIdByUserId(Integer userId) {
        return userDao.getRootFolderIdByUserId(userId);
    }

    public User getUserByUserName(String userName) {
        User user = userDao.getUserByUserName(userName);
        return user;
    }

    //从redis中获取用户网址分享数量的排序数据
    public PageEntity<User> getShareUserOrder(Integer page,int limit) {
        PageEntity<User> pageEntity=null;
        List<User> userList=null;

        //先去redis中获取主页缓存数据
        Object data = redisTemplate.opsForValue().get(PublicVar.userShareData+page);
        if(data!=null){
            return JSON.parseObject((String) data, new TypeReference<PageEntity<User>>() {});
        }else{
            Set set = redisTemplate.opsForZSet().reverseRangeWithScores(PublicVar.userShareScore, (page - 1) * limit, page*limit - 1);
            if(set.size()==0){//如果没有结果集，则直接返回
                return new PageEntity<>(0L, new ArrayList<User>(), page);
            }
            Object[] objects = set.toArray();
            userList=new ArrayList<>();
            for(int i=0;i<objects.length;i++){
                DefaultTypedTuple object = (DefaultTypedTuple)objects[i];
                User user=new User();
                user.setUserName(RedisUtil.toString(object.getValue()));
                user.setShareNumber(RedisUtil.toInt(object.getScore()));
                userList.add(user);
            }
            pageEntity = new PageEntity<>(Long.valueOf(userList.size()), userList, set.size()<limit?page:page+1);
            if(PublicVar.updateTime>0){
                redisTemplate.opsForValue().set(PublicVar.userShareData+page,JSON.toJSONString(pageEntity),PublicVar.updateTime, TimeUnit.MINUTES);//在将数据缓存在redis中,并并且设置1分钟过期
            }
        }

        return pageEntity;
    }

    //从redis中获取用户被点赞数量的排序数据
    public PageEntity<User> getGoodUserOrder(Integer page, int limit) {
        PageEntity<User> pageEntity=null;
        List<User> userList=null;

        //先去redis中获取主页缓存数据
        Object data = redisTemplate.opsForValue().get(PublicVar.userGoodData+page);
        if(data!=null){
            return JSON.parseObject((String) data, new TypeReference<PageEntity<User>>() {});
        }else{
            Set set = redisTemplate.opsForZSet().reverseRangeWithScores(PublicVar.userGoodScore, (page - 1) * limit, page*limit - 1);
            if(set.size()==0){//如果没有结果集，则直接返回
                return new PageEntity<>(0L, new ArrayList<User>(), page);
            }
            Object[] objects = set.toArray();
            userList=new ArrayList<>();
            for(int i=0;i<objects.length;i++){
                DefaultTypedTuple object = (DefaultTypedTuple)objects[i];
                User user=new User();
                user.setUserName(RedisUtil.toString(object.getValue()));
                user.setGoodNumber(RedisUtil.toInt(object.getScore()));
                userList.add(user);
            }
            pageEntity = new PageEntity<>(Long.valueOf(userList.size()), userList, set.size()<limit?page:page+1);
            if(PublicVar.updateTime>0){
                redisTemplate.opsForValue().set(PublicVar.userGoodData+page,JSON.toJSONString(pageEntity),PublicVar.updateTime, TimeUnit.MINUTES);//在将数据缓存在redis中,并并且设置1分钟过期
            }
        }

        return pageEntity;
    }

    public int updateUserShareNumberByUserNameBatch(List<User> list) {
        if(list.size()>0){
            return userDao.updateUserShareNumberByUserNameBatch(list);
        }else {
            return 0;
        }
    }

    public int updateUserGoodNumberByUserNameBatch(List<User> list) {
        if(list.size()>0){
            return userDao.updateUserGoodNumberByUserNameBatch(list);
        }else {
            return 0;
        }

    }

    //添加会员过期日期
    public void addDeadline(HttpSession session,Order order,User sessionUser){
        int months=0;
        switch(order.getType()){
            case 1://年卡
                months=12;//12个月
                break;
            case 2:
                months=3;//3个月
                break;
            case 3:
                months=1;//1个月
                break;
            default:
                break;
        }

        User user = userDao.getUserByUserId(sessionUser);
        if(user.getDeadline().getTime()>new Date().getTime()){//是会员
            int i = userDao.addDeadline(true, months, sessionUser.getId());
            if(i!=1){
                throw new AlipayException(null,order);//退款
            }
        }else{//不是会员
            int i = userDao.createDeadline(true, months, sessionUser.getId());
            if(i!=1){
                throw new AlipayException(null,order);//退款
            }
        }

        updateSessionUser(session,user);//更新session中的user信息

    }

    //更新session中的user信息
    public void updateSessionUser(HttpSession session,User user){
        User updateUser = getUserByUserId(user);
        session.setAttribute("user",updateUser);
    }


}
