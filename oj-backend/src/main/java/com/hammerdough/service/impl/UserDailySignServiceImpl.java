package com.hammerdough.service.impl;

import com.hammerdough.entity.UserDailySign;
import com.hammerdough.mapper.UserDailySignMapper;
import com.hammerdough.service.UserDailySignService;
import com.hammerdough.vo.UserDailySignVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDailySignServiceImpl implements UserDailySignService {

    @Autowired
    private UserDailySignMapper userDailySignMapper;

    @Override
    public List<UserDailySignVO> getUserMonthSign(Integer userId, Integer year, Integer month) {
        List<UserDailySign> dateList = userDailySignMapper.getMonthSignList(userId,year,month);
        return dateList.stream().map(sign->{
            UserDailySignVO vo = new UserDailySignVO();
            vo.setSignDate(sign.getSignDate().toString());
            return vo;
        }).collect(Collectors.toList());
    }
}
