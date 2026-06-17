package com.hammerdough.service.impl;

import com.hammerdough.entity.OjSubmitRecord;
import com.hammerdough.entity.VOjSubmitRecordList;
import com.hammerdough.mapper.OjSubmitRecordMapper;
import com.hammerdough.service.SubmitRecordService;
import com.hammerdough.vo.SubmitRecordDetailVO;
import com.hammerdough.vo.SubmitRecordListVO;
import com.hammerdough.vo.UserDailyStatVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class SubmitRecordServiceImpl implements SubmitRecordService {

    @Autowired
    private OjSubmitRecordMapper ojSubmitRecordMapper;


    @Override
    public List<SubmitRecordListVO> getRecordList(Integer userId, Integer problemId) {
        List<VOjSubmitRecordList> list = ojSubmitRecordMapper.selectListByView(userId,problemId);

        return list.stream().map(item->{
           SubmitRecordListVO vo = new SubmitRecordListVO();
            vo.setId(item.getId());
            vo.setLanguage(item.getLanguage());
            vo.setSubmitTime(item.getSubmitTime());
            vo.setRunTime(item.getRunTime());
            vo.setRunMemory(item.getRunMemory());

            if(item.getPassCase()==0){
                vo.setPassRate("0%");
            }else{
                int rate = (int)((double)item.getPassCase()/item.getTotalCase()*100);
                vo.setPassRate(rate+"%");
            }

            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public SubmitRecordDetailVO getRecordDetail(Integer userId, Integer id, Integer problemId) {
        OjSubmitRecord record = ojSubmitRecordMapper.selectDetailById(id,userId,problemId);

        if(record==null){
            return null;
        }

        SubmitRecordDetailVO vo = new SubmitRecordDetailVO();
        BeanUtils.copyProperties(record,vo);
        if(record.getPassCase()==0){
            vo.setPassRate("0%");
        }else{
            int rate = (int)((double)record.getPassCase()/record.getTotalCase()*100);
            vo.setPassRate(rate+"%");
        }


        return vo;
    }

    @Override
    public void insertRecord(OjSubmitRecord record) {
        ojSubmitRecordMapper.insert(record);
    }
}
