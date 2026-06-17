package com.hammerdough.controller;


import com.hammerdough.common.Result;
import com.hammerdough.dto.JudgeSubmitDTO;
import com.hammerdough.service.JudgeService;
import com.hammerdough.vo.JudgeResultVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/judge")
public class JudgeController {

    @Autowired
    private JudgeService judgeService;

    @PostMapping("/submit")
    public Result<JudgeResultVO> judge(HttpServletRequest request,
                       @RequestBody JudgeSubmitDTO submitDTO) {
        Integer userId = (Integer) request.getAttribute("userId");
        if(userId==null){
            return Result.error("未登录");
        }

        JudgeResultVO result = judgeService.judge(submitDTO,userId);
        return Result.success(result);
    }

}
