package com.hammerdough.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserDailyStat implements Serializable {
    private Integer id;
    private Integer userId;
    private LocalDate statDate;
    private Integer totalMinute;
    private Integer finishCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
