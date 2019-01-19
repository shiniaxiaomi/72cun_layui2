package com.lyj.controller;

import com.lyj.model.Result;
import com.lyj.service.PhoneService;
import com.lyj.util.PhoneMessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * Created by Administrator on 2019/1/19.
 */
@RestController
@RequestMapping("/phone")
public class PhoneController {
    @Autowired
    PhoneService phoneService;


    @RequestMapping("/isPhoneNumberExist")
    public int isPhoneNumberExist(String phoneNumber){
        return phoneService.isPhoneNumberExist(phoneNumber);
    }

    /**
     * 发送验证码请求
     */
    @RequestMapping("/sendCode")
    public Result sendCode(HttpSession session, String phoneNumber){
        return PhoneMessageUtil.sendPhoneMessage(session,phoneNumber);
    }

}
