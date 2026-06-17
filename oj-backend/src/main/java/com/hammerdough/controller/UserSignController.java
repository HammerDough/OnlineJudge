package com.hammerdough.controller;


import com.hammerdough.common.Result;
import com.hammerdough.dto.SignCalendarQueryDTO;
import com.hammerdough.service.UserDailySignService;
import com.hammerdough.vo.UserDailySignVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserSignController {

    @Autowired
    private UserDailySignService userDailySignService;


    @GetMapping("/daily-sign")
    public Result<List<UserDailySignVO>> getUserDailySign(HttpServletRequest request, SignCalendarQueryDTO dto){
        Integer userId = (Integer)request.getAttribute("userId");
        List<UserDailySignVO> list = userDailySignService.getUserMonthSign(userId,dto.getYear(),dto.getMonth());
        return Result.success(list);
    }
}
