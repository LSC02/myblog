package com.lsc.blog.service;

import com.lsc.blog.po.User;

public interface UserService {
    User checkUser(String username,String password);
}
