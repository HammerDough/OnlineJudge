package com.hammerdough.service;

import com.hammerdough.dto.JudgeSubmitDTO;
import com.hammerdough.vo.JudgeResultVO;

public interface JudgeService {
    JudgeResultVO judge(JudgeSubmitDTO submitDTO, Integer userId);
}
