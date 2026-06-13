package com.hammerdough.mapper;

import com.hammerdough.entity.UserDailyStat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface UserDailyStatMapper {

    @Select("select stat_date,total_minute,finish_count" +
            " from user_daily_stat where user_id = #{userId} and stat_date between #{start} and #{end} order by stat_date asc")
    List<UserDailyStat> selectByUserIdAndDateRange(Integer userId, LocalDate start, LocalDate end);
}
