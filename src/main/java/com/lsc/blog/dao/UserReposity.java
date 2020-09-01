package com.lsc.blog.dao;

import com.lsc.blog.po.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserReposity extends JpaRepository<User,Long>{
    User findByUsernameAndPassword(String username,String password);

}
