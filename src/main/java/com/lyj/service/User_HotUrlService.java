package com.lyj.service;

import com.lyj.dao.User_HotUrlDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Administrator on 2019/3/2.
 */

@Service
public class User_HotUrlService {

    @Autowired
    User_HotUrlDao user_hotUrlDao;

    public int deleteUser_HotUrlByUrlId(Integer urlId) {
        return user_hotUrlDao.deleteUser_HotUrlByUrlId(urlId);
    }


    public int deleteUser_HotUrlByUrlIdBatch(List<Integer> ids) {
        if(ids.size()>0){
            return user_hotUrlDao.deleteUser_HotUrlByUrlIdBatch(ids);
        }else{
            return 0;
        }
    }
}
