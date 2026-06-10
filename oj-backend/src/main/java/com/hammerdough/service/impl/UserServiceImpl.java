package com.hammerdough.service.impl;

import com.hammerdough.dto.UserDTO;
import com.hammerdough.entity.User;
import com.hammerdough.mapper.UserMapper;
import com.hammerdough.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    //密码加密
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    //初次注册随机昵称
    private final Random random = new Random();


    @Override
    public boolean register(UserDTO userDTO) {
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();

        if(password.length()<6){
            return false;
        }

        User existUser = userMapper.selectByUsername(username);

        if(existUser != null){
            return false;
        }

        String randomNick = generateRandomNickname();

        String encodePwd = passwordEncoder.encode(password);

        User user = new User();
        user.setUsername(username);
        user.setPassword(encodePwd);
        user.setNickname(randomNick);

        int rows = userMapper.insert(user);
        return rows > 0;
    }

    private String generateRandomNickname(){
        String prefix = "OJ_";
        int num = random.nextInt(1000);
        return prefix +String.format("%03d",num);
    }
}
