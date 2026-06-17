package com.hammerdough.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ProblemTestCase implements Serializable {
    private Integer id;
    private Integer problemId;
    private String inputContent;
    private String outputContent;
    private Integer runFlag;
    private Integer isEnabled;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer isDeleted;
}
