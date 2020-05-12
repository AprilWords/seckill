package com.learn.miaosha.dao;

import com.learn.miaosha.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserDao {
    @Select("select * from user where id= #{id}")
    public User getById(int id);
}
