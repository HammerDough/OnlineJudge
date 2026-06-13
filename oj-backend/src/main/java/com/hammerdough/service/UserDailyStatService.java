package com.hammerdough.service;

import com.hammerdough.vo.UserDailyStatVO;

import java.time.LocalDate;
import java.util.List;

public interface UserDailyStatService {

    List<UserDailyStatVO> getUserDailyStat(Integer userId, LocalDate start, LocalDate end);
}
