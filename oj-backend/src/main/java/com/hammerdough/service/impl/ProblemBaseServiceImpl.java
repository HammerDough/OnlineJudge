package com.hammerdough.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hammerdough.common.PageResult;
import com.hammerdough.dto.ProblemQueryDTO;
import com.hammerdough.entity.ProblemBase;
import com.hammerdough.entity.ProblemDetail;
import com.hammerdough.mapper.ProblemBaseMapper;
import com.hammerdough.mapper.ProblemDetailMapper;
import com.hammerdough.service.ProblemBaseService;
import com.hammerdough.vo.ProblemBaseVO;
import com.hammerdough.vo.ProblemDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProblemBaseServiceImpl implements ProblemBaseService {

    @Autowired
    private ProblemBaseMapper problemBaseMapper;

    @Autowired
    private ProblemDetailMapper problemDetailMapper;

    @Override
    public PageResult<ProblemBaseVO> getProblemList(ProblemQueryDTO queryDTO) {
        Integer difficultyNum = null;
        if(queryDTO.getDifficulty()!=null){
             difficultyNum= Integer.parseInt(queryDTO.getDifficulty());
        }


        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());

        Page<ProblemBase> page = problemBaseMapper.selectProblemList(
                queryDTO.getSearchField(),
                queryDTO.getKeyword(),
                difficultyNum
        );

        long total = page.getTotal();
        List<ProblemBase> problemList = page.getResult();

        List<ProblemBaseVO> voList = problemList.stream().map(problem->{
            ProblemBaseVO vo = new ProblemBaseVO();
            vo.setId(problem.getId());
            vo.setTitle(problem.getTitle());

            switch (problem.getDifficulty()) {
                case 1:
                    vo.setDifficulty("简单");
                    break;
                case 2:
                    vo.setDifficulty("中等");
                    break;
                case 3:
                    vo.setDifficulty("困难");
                    break;
                default:
                    vo.setDifficulty("未知");
            }

            if(problem.getSubmitCount()==0){
                vo.setPassRate("0%");
            }else{
                int rate = (int)((double)problem.getAcceptCount()/problem.getSubmitCount()*100);
                vo.setPassRate(rate+"%");
            }

            return vo;
        }).collect(Collectors.toList());

        return new PageResult<>(page.getTotal(),voList);
    }

    @Override
    public ProblemDetailVO getFullDetailById(Integer problemId) {
        ProblemBase base = problemBaseMapper.selectBaseById(problemId);
        if(base == null){
            throw new RuntimeException("题目不存在或已删除");
        }

        ProblemDetail detail = problemDetailMapper.selectDetailByProblemId(problemId);
        if(detail == null){
            throw new RuntimeException("题目详情缺失");
        }

        ProblemDetailVO vo = new ProblemDetailVO();

        vo.setId(base.getId());
        vo.setTitle(base.getTitle());

        String diffText = switch (base.getDifficulty()){
            case 1->"简单";
            case 2->"中等";
            case 3->"困难";
            default->"未知";
        };

        vo.setDifficultyText(diffText);
        int submit = base.getSubmitCount();
        int accept = base.getAcceptCount();
        String rate = submit == 0 ? "0%" :(int)((double)accept/submit*100)+"%";
        vo.setPassRate(rate);

        // 赋值详情内容
        vo.setDescription(detail.getDescription());
        vo.setInputDescription(detail.getInputDescription());
        vo.setOutputDescription(detail.getOutputDescription());
        vo.setSampleInput(detail.getSampleInput());
        vo.setSampleOutput(detail.getSampleOutput());
        vo.setHint(detail.getHint());
        vo.setTimeLimit(detail.getTimeLimit());
        vo.setMemoryLimit(detail.getMemoryLimit());

        return vo;
    }
}
