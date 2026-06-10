package com.hammerdough.service;

import com.hammerdough.dto.UserDTO;
import com.hammerdough.entity.User;
import org.springframework.stereotype.Service;


public interface UserService {
    /**
     * 用户注册
     * @param userDTO
     * @return true 注册成功 / false 注册失败
     */
    boolean register(UserDTO userDTO);

    User login(UserDTO userDTO);
}
