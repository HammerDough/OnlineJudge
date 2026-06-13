package com.hammerdough.mapper;

import com.hammerdough.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {

    @Select("select * from user where username = #{username}")
    User selectByUsername(String username);

    @Insert("insert into user(username,password,nickname)" +
            "values (#{username},#{password},#{nickname})")
    int insert(User user);

    @Select("select * from user where user_id=#{userId}")
    User selectByUserId(Integer userId);

    @Update("update user set nickname=#{nickname},avatar=#{avatar},update_time=now() where user_id=#{userId}")
    void updateUserInfo(Integer userId, String nickname, String avatar);
}
