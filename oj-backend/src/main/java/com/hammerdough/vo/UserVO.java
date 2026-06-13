package com.hammerdough.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class UserVO implements Serializable {
    private Integer userId;
    private String username;
    private String nickname;
    private String avatar;
    private Integer totalMinute;
    private Integer finishProblemNum;
    private LocalDateTime createTime;
}