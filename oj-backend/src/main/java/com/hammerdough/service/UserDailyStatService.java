package com.hammerdough.service;

import com.hammerdough.dto.DailyMinuteDTO;
import com.hammerdough.vo.UserDailyStatVO;

import java.time.LocalDate;
import java.util.List;

public interface UserDailyStatService {

    List<UserDailyStatVO> getUserDailyStat(Integer userId, LocalDate start, LocalDate end);

    void addUserDailyMinute(Integer userId, DailyMinuteDTO dto);
}
