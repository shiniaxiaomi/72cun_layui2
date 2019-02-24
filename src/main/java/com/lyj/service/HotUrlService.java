package com.lyj.service;

import com.lyj.dao.HotUrlDao;
import com.lyj.exception.MessageException;
import com.lyj.model.HotUrl;
import com.lyj.model.linkModel.User_HotUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Administrator on 2019/2/24.
 */

@Service
public class HotUrlService {

    @Autowired
    HotUrlDao hotUrlDao;

    public int incrClickNumber(int urlId) {
        return hotUrlDao.incrClickNumber(urlId);
    }

    public void addHotUrl(HotUrl hotUrl) {
        int i = hotUrlDao.addHotUrl(hotUrl);
        if(i!=1){
            throw new MessageException("共享网址热点数据添加失败!");
        }
    }

    public int isIncredGoodNumber(User_HotUrl user_hotUrl) {
        return hotUrlDao.isIncredGoodNumber(user_hotUrl);
    }

    public int incrGoodNumber(int urlId) {
        return hotUrlDao.incrGoodNumber(urlId);
    }

    public void markIsIncredGoodNumber(User_HotUrl user_hotUrl) {
        int i = hotUrlDao.markIsIncredGoodNumber(user_hotUrl);
        if(i==0){
            throw new MessageException("点赞标记失败！");
        }
    }
}
