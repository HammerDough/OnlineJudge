package com.hammerdough.controller;


import com.hammerdough.common.Result;
import com.hammerdough.service.SubmitRecordService;
import com.hammerdough.vo.SubmitRecordDetailVO;
import com.hammerdough.vo.SubmitRecordListVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/submit")
public class SubmitRecordController {

    @Autowired
    private SubmitRecordService submitRecordService;


    @GetMapping("/list")
    public Result<List<SubmitRecordListVO>> getRecordList(HttpServletRequest request,
          @RequestParam(value = "problemId", required = false) Integer problemId){
        if(problemId == null){
            return Result.error("题目ID不能为空");
        }
        Integer userId = (Integer)request.getAttribute("userId");
        List<SubmitRecordListVO> list = submitRecordService.getRecordList(userId,problemId);
        return Result.success(list);
    }


    @GetMapping("/detail")
    public Result<SubmitRecordDetailVO> getRecordDetailById(HttpServletRequest request,
        @RequestParam(value = "id", required = false) Integer id,
        @RequestParam(value = "problemId", required = false) Integer problemId){

        if(id == null || problemId == null){
            return Result.error("参数不能为空");
        }

        Integer userId = (Integer)request.getAttribute("userId");

        SubmitRecordDetailVO vo = submitRecordService.getRecordDetail(userId,id,problemId);
        if(vo==null){
            return Result.error("记录不存在");
        }
        return Result.success(vo);
    }
}
