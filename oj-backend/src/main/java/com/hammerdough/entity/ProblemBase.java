package com.hammerdough.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ProblemBase implements Serializable {
    private Integer id;
    private String title;
    private Integer difficulty;
    private Integer submitCount;
    private Integer acceptCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer isDeleted;
}
