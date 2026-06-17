package com.hammerdough.mapper;

import com.hammerdough.entity.ProblemTestCase;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProblemTestCaseMapper {

    List<ProblemTestCase> selectRunCaseByProblemId(Integer problemId);

    List<ProblemTestCase> selectAllValidCaseByProblemId(Integer problemId);
}
