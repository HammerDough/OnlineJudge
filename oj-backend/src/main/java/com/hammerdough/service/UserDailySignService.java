package com.hammerdough.service;

import com.hammerdough.vo.UserDailySignVO;

import java.util.List;

public interface UserDailySignService {
    List<UserDailySignVO> getUserMonthSign(Integer userId, Integer year, Integer month);
}
