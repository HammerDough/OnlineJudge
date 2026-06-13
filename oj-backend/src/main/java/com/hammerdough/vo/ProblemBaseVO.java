package com.hammerdough.vo;

import lombok.Data;

@Data
public class ProblemBaseVO {
    private Integer id;
    private String title;
    private String difficulty;
    private String passRate;
}
