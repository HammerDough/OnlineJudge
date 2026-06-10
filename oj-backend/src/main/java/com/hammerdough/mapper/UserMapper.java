package com.hammerdough.mapper;

import com.hammerdough.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("select * from user where username = #{username}")
    User selectByUsername(String username);

    @Insert("insert into user(username,password,nickname)" +
            "values (#{username},#{password},#{nickname})")
    int insert(User user);
}
