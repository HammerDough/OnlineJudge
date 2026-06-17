package com.hammerdough.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubmitRecordListVO {
    private Integer id;
    private String language;
    private LocalDateTime submitTime;
    private Integer runTime;
    private Integer runMemory;
    private String passRate;
}
