package com.hammerdough.mapper;

import com.hammerdough.entity.UserDailyStat;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface UserDailyStatMapper {

    @Select("select stat_date,total_minute,finish_count" +
            " from user_daily_stat where user_id = #{userId} and stat_date between #{start} and #{end} order by stat_date asc")
    List<UserDailyStat> selectByUserIdAndDateRange(Integer userId, LocalDate start, LocalDate end);

    // --- 新增判题场景接口 ---
    /**
     * 查询用户当日统计记录（存在则更新，不存在则插入）
     */
    @Select("SELECT id FROM user_daily_stat WHERE user_id = #{userId} AND stat_date = #{statDate}")
    Integer selectIdByUserIdAndDate(Integer userId, LocalDate statDate);

    /**
     * 新增用户当日统计记录
     */
    @Insert("INSERT INTO user_daily_stat(user_id, stat_date, finish_count, total_minute, update_time) " +
            "VALUES(#{userId}, #{statDate}, #{finishCount}, IFNULL(#{totalMinute}, 0), NOW())")
    int insertUserDailyStat(UserDailyStat stat);

    /**
     * 更新用户当日做题时长（+addMinute）
     */
    @Update("UPDATE user_daily_stat SET total_minute = total_minute + #{addMinute}, update_time = NOW() WHERE id = #{id}")
    int addDailyMinute(@Param("id") Integer id, @Param("addMinute") int addMinute);

    /**
     * 更新用户当日完成题目数（+1）
     */
    @Update("UPDATE user_daily_stat SET finish_count = finish_count + 1, update_time = NOW() WHERE id = #{id}")
    int incrDailyFinishCount(Integer id);
}
