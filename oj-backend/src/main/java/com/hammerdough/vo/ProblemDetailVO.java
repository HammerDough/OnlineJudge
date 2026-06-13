package com.hammerdough.vo;

import lombok.Data;

@Data
public class ProblemDetailVO {
    // 来自 problem_base
    private Integer id;
    private String title;
    private String difficultyText; // 1->简单 2->中等 3->困难
    private String passRate;       // 通过率

    // 来自 problem_detail
    private String description;
    private String inputDescription;
    private String outputDescription;
    private String sampleInput;
    private String sampleOutput;
    private String hint;
    private Integer timeLimit;
    private Integer memoryLimit;
}
