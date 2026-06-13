package com.hammerdough.mapper;

import com.github.pagehelper.Page;
import com.hammerdough.entity.ProblemBase;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProblemBaseMapper {

     Page<ProblemBase> selectProblemList(
            @Param("searchField") String searchField,
            @Param("keyword") String keyword,
            @Param("difficulty") Integer difficulty
    );

     ProblemBase selectBaseById(Integer id);
}
