package com.hammerdough.dto;

import lombok.Data;

@Data
public class JudgeSubmitDTO {
    private Integer problemId;
    private String code;
    private String language;
    private Integer operateType;
}
