package com.hammerdough.entity;


import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserDailySign {
    private Integer id;
    private Integer userId;
    private LocalDate signDate;
    private LocalDateTime createTime;
}
