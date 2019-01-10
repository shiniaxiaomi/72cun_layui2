package com.lyj.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lyj.dao.AdminDao;
import com.lyj.model.User;
import com.lyj.util.PageEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Yingjie.Lu on 2018/9/17.
 */

@Service
public class AdminService {

    @Autowired
    AdminDao adminDao;

    public PageInfo<User> getUsers(Integer page, Integer limit) {
        PageHelper.startPage(page, limit);
        List<User> users = adminDao.getUsers();
        return new PageInfo<>(users);
    }
}
