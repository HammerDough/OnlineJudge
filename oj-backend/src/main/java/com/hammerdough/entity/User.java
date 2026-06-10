package com.hammerdough.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class User implements Serializable {

    private Integer userId;
    private String username;
    private String password;
    private String nickname;
    private String avatar;
    private Integer totalMinute;
    private Integer finishProblemNum;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
