package com.hammerdough.service.impl;

import com.hammerdough.entity.ProblemTestCase;
import com.hammerdough.mapper.ProblemTestCaseMapper;
import com.hammerdough.service.ProblemTestCaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProblemTestCaseServiceImpl implements ProblemTestCaseService {

    @Autowired
    private ProblemTestCaseMapper problemTestCaseMapper;

    @Override
    public List<ProblemTestCase> getRunTestCase(Integer problemId) {
        return problemTestCaseMapper.selectRunCaseByProblemId(problemId);
    }

    @Override
    public List<ProblemTestCase> getSubmitTestCase(Integer problemId) {
        return problemTestCaseMapper.selectAllValidCaseByProblemId(problemId);
    }
}