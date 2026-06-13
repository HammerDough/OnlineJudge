package com.hammerdough.service.impl;

import com.hammerdough.entity.UserDailyStat;
import com.hammerdough.mapper.UserDailyStatMapper;
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
}
