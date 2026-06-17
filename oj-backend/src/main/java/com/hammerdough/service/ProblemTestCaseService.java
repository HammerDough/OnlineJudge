package com.hammerdough.service;

import com.hammerdough.entity.ProblemTestCase;
import java.util.List;

public interface ProblemTestCaseService {


    List<ProblemTestCase> getRunTestCase(Integer problemId);

    List<ProblemTestCase> getSubmitTestCase(Integer problemId);
}