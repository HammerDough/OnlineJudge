package com.hammerdough.controller;

import com.hammerdough.common.Result;
import com.hammerdough.dto.DailyMinuteDTO;
import com.hammerdough.service.UserDailyStatService;
import com.hammerdough.vo.UserDailyStatVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/user/daily-stat")
public class UserStatController {

    @Autowired
    private UserDailyStatService userDailyStatService;


    @GetMapping("/{userId}")
    public Result<List<UserDailyStatVO>> getUserDailyStat(
            @PathVariable Integer userId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate){
        if(userId==null || userId<=0){
            return Result.error("用户id不合法");
        }

        LocalDate end;
        LocalDate start;

        if(startDate==null || endDate==null){
            end=LocalDate.now();
            start = end.minusDays(364);
        }else{
            try{
                start = LocalDate.parse(startDate);
                end = LocalDate.parse(endDate);
            }catch(Exception e){
                return Result.error("日期格式错误，请使用 YYYY-MM-DD 格式");
            }
        }

        List<UserDailyStatVO> statList = userDailyStatService.getUserDailyStat(userId,start,end);
        return Result.success(statList);
    }


    @PostMapping("/add-minute")
    public Result<Void> addDailyMinute(HttpServletRequest request,
                                       @RequestBody DailyMinuteDTO dto){
        Integer userId = (Integer)request.getAttribute("userId");
        userDailyStatService.addUserDailyMinute(userId,dto);
        return Result.success(null);
    }
}
