package com.lsc.blog.service;

import com.lsc.blog.dao.UserReposity;
import com.lsc.blog.po.User;
import com.lsc.blog.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserReposity userReposity;

    @Override
    public User checkUser(String username, String password) {
        User user=userReposity.findByUsernameAndPassword(username, MD5Util.code(password));

        return user;
    }
}
