package com.hammerdough.mapper;


import com.hammerdough.entity.OjSubmitRecord;
import com.hammerdough.entity.VOjSubmitRecordList;
import com.hammerdough.vo.SubmitRecordListVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OjSubmitRecordMapper {


    @Select("select * from v_oj_submit_record_list where user_id=#{userId} and problem_id=#{problemId} order by submit_time desc")
    List<VOjSubmitRecordList> selectListByView(@Param("userId")Integer userId,@Param("problemId") Integer problemId);


    @Select("select * from oj_submit_record where id=#{id} and user_id=#{userId} and problem_id=#{problemId}")
    OjSubmitRecord selectDetailById(@Param("id") Integer id,@Param("userId")Integer userId,@Param("problemId") Integer problemId);


    @Insert("INSERT INTO oj_submit_record(user_id,problem_id,language,submit_code,run_time,run_memory,total_case,pass_case) " +
            "VALUES(#{userId},#{problemId},#{language},#{submitCode},#{runTime},#{runMemory},#{totalCase},#{passCase})")
    int insert(OjSubmitRecord record);

}
