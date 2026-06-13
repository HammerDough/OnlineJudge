package com.hammerdough.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ProblemDetail implements Serializable {
    private Integer id;
    private Integer problemId;
    private String description;
    private String inputDescription;
    private String outputDescription;
    private String sampleInput;
    private String sampleOutput;
    private String hint;
    private Integer timeLimit;
    private Integer memoryLimit;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
