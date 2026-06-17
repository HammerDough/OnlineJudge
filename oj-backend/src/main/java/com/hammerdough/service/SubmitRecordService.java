package com.hammerdough.service;

import com.hammerdough.entity.OjSubmitRecord;
import com.hammerdough.vo.SubmitRecordDetailVO;
import com.hammerdough.vo.SubmitRecordListVO;

import java.util.List;

public interface SubmitRecordService {
    List<SubmitRecordListVO> getRecordList(Integer userId, Integer problemId);

    SubmitRecordDetailVO getRecordDetail(Integer userId, Integer id, Integer problemId);

    void insertRecord(OjSubmitRecord record);
}
