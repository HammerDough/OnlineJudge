package com.hammerdough.entity;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VOjSubmitRecordList {
    private Integer id;
    private Integer userId;
    private Integer problemId;
    private String language;
    private LocalDateTime submitTime;
    private Integer runTime;
    private Integer runMemory;
    private Integer totalCase;
    private Integer passCase;
}
