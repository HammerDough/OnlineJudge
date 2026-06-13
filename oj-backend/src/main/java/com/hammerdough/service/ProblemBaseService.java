package com.hammerdough.service;

import com.hammerdough.common.PageResult;
import com.hammerdough.dto.ProblemQueryDTO;
import com.hammerdough.vo.ProblemBaseVO;
import com.hammerdough.vo.ProblemDetailVO;

public interface ProblemBaseService {
    PageResult<ProblemBaseVO> getProblemList(ProblemQueryDTO queryDTO);

    ProblemDetailVO getFullDetailById(Integer problemId);
}
