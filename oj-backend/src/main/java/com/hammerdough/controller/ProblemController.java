package com.hammerdough.controller;


import com.hammerdough.common.PageResult;
import com.hammerdough.common.Result;
import com.hammerdough.dto.ProblemQueryDTO;
import com.hammerdough.service.ProblemBaseService;
import com.hammerdough.vo.ProblemBaseVO;
import com.hammerdough.vo.ProblemDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/problem")
public class ProblemController {

    @Autowired
    private ProblemBaseService problemBaseService;

    @GetMapping("/list")
    public Result<PageResult<ProblemBaseVO>> getProblemList(ProblemQueryDTO queryDTO){
        PageResult<ProblemBaseVO> pageResult = problemBaseService.getProblemList(queryDTO);
        return Result.success(pageResult);
    }

    @GetMapping("/detail/{id}")
    public Result<ProblemDetailVO> getProblemDetail(@PathVariable Integer id){
        ProblemDetailVO vo = problemBaseService.getFullDetailById(id);
        return Result.success(vo);
    }
}
