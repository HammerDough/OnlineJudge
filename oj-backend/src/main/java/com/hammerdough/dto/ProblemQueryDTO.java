package com.hammerdough.dto;

import lombok.Data;

@Data
public class ProblemQueryDTO {

    private String searchField;
    private String keyword;
    private String difficulty;
    private Integer pageNum = 1;
    private Integer pageSize = 15;
}
