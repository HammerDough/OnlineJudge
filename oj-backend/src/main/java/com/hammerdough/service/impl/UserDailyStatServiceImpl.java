package com.hammerdough.service.impl;

import com.hammerdough.dto.DailyMinuteDTO;
import com.hammerdough.entity.UserDailyStat;
import com.hammerdough.mapper.UserDailyStatMapper;
import com.hammerdough.mapper.UserMapper;
import com.hammerdough.service.UserDailyStatService;
import com.hammerdough.vo.UserDailyStatVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDailyStatServiceImpl implements UserDailyStatService {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private UserDailyStatMapper userDailyStatMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<UserDailyStatVO> getUserDailyStat(Integer userId, LocalDate start, LocalDate end) {
       List<UserDailyStat> statList = userDailyStatMapper.selectByUserIdAndDateRange(userId,start,end);
       return statList.stream().map(stat->{
           UserDailyStatVO vo = new UserDailyStatVO();
           vo.setDate(stat.getStatDate().format(formatter));
           vo.setTotalMinute(stat.getTotalMinute());
           vo.setFinishCount(stat.getFinishCount());
           return vo;
       }).collect(Collectors.toList());
    }

    @Override
    public void addUserDailyMinute(Integer userId, DailyMinuteDTO dto) {
        LocalDate statDate = LocalDate.parse(dto.getDate());
        int addMinute = dto.getAddMinute();

        // 2. 查询当天是否已有统计行
        Integer recordId = userDailyStatMapper.selectIdByUserIdAndDate(userId, statDate);
        if (recordId == null) {
            // 当天无记录：新建空记录，finish_count=0，total_minute=新增时长
            UserDailyStat newStat = new UserDailyStat();
            newStat.setUserId(userId);
            newStat.setStatDate(statDate);
            newStat.setFinishCount(0);
            newStat.setTotalMinute(addMinute);
            userDailyStatMapper.insertUserDailyStat(newStat);
        } else {
            // 已有记录：仅累加时长，不改动完成题目数
            userDailyStatMapper.addDailyMinute(recordId, addMinute);
        }
        userMapper.addTotalMinute(userId, addMinute);
    }
}
