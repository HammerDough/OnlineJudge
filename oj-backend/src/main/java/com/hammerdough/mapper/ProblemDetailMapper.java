package com.hammerdough.mapper;

import com.hammerdough.entity.ProblemDetail;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProblemDetailMapper {
    ProblemDetail selectDetailByProblemId(Integer problemId);
}
