package com.hammerdough.mapper;

import com.hammerdough.entity.User;
import org.apache.ibatis.annotations.*;

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

    /**
     * 更新用户累计做题时长（分钟）
     */
    @Update("UPDATE user SET total_minute = total_minute + #{addMinute}, update_time = NOW() WHERE user_id = #{userId}")
    int addTotalMinute(@Param("userId") Integer userId, @Param("addMinute") int addMinute);

    /**
     * 更新用户完成题目总数（+1）
     */
    @Update("UPDATE user SET finish_problem_num = finish_problem_num + 1, update_time = NOW() WHERE user_id = #{userId}")
    int incrFinishProblemNum(Integer userId);
}
