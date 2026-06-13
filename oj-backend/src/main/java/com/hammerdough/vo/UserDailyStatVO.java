package com.hammerdough.vo;

import lombok.Data;

@Data
public class UserDailyStatVO {

    private String date;
    private Integer totalMinute;
    private Integer finishCount;
}
