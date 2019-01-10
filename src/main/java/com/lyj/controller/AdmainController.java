package com.lyj.controller;

import com.github.pagehelper.PageInfo;
import com.lyj.model.Result;
import com.lyj.model.User;
import com.lyj.service.AdminService;
import com.lyj.service.UserService;
import com.lyj.util.PageEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Yingjie.Lu on 2019/1/10.
 */

@RestController
@RequestMapping("/admin")
public class AdmainController {

    @Autowired
    AdminService adminService;

    @Autowired
    UserService userService;


    @RequestMapping("/getUsers")
    public PageEntity<User> getUsers(Integer page, Integer limit){
        PageInfo<User> pageInfo = adminService.getUsers(page, limit);
        return new PageEntity<>(pageInfo.getTotal(),pageInfo.getList());
    }

     @RequestMapping("/deleteUserById")
    public Result deleteUserById(Integer id){
         return userService.deleteUserById(id);
    }



}
