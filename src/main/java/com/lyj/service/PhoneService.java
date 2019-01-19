package com.lyj.service;

import com.lyj.dao.PhoneDao;
import com.lyj.dao.UserDao;
import com.lyj.model.Result;
import com.lyj.model.User;
import com.lyj.util.ResultUtil;
import com.lyj.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by Yingjie.Lu on 2018/9/17.
 */

@Service
public class PhoneService {


    @Autowired
    PhoneDao phoneDao;


    public int isPhoneNumberExist(String phoneNumber) {
        return phoneDao.isPhoneNumberExist(phoneNumber);
    }
}
