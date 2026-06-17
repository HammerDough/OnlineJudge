package com.hammerdough.mapper;


import com.hammerdough.entity.UserDailySign;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserDailySignMapper {

    @Select("select sign_date from user_daily_sign " +
            "where user_id=#{userId} " +
            "and sign_date >= str_to_date(concat(#{year},'-',#{month},'-01'), '%Y-%m-%d') " +
            "and sign_date <=last_day(str_to_date(concat(#{year},'-',#{month},'-01'), '%Y-%m-%d'))")
    List<UserDailySign> getMonthSignList(Integer userId, Integer year, Integer month);


    @Insert("insert into user_daily_sign(user_id,sign_date) values (#{userId},curdate()) on duplicate key update id=id")
    void insertSign(Integer userId);
}
