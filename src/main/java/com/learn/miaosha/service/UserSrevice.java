package com.learn.miaosha.service;

import com.learn.miaosha.dao.UserDao;
import com.learn.miaosha.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserSrevice {
    @Autowired
    UserDao userDao;
    public User getById(int id){
        return userDao.getById(id);
    }
}
