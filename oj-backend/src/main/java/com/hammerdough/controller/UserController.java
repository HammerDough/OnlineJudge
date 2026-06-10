package com.hammerdough.controller;


import com.hammerdough.common.Result;
import com.hammerdough.dto.UserDTO;
import com.hammerdough.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result<String> register(@RequestBody UserDTO userDTO) {
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();

        if(username ==null || "".equals(username.trim())){
            return Result.error("账号不能为空");
        }
        if(password ==null || "".equals(password.trim())){
            return Result.error("密码不能为空");
        }

        boolean registerFlag = userService.register(userDTO);
        if(registerFlag){
            return Result.success();
        }else{
            return Result.error("注册失败，账号已存在或密码不合法");
        }
    }




}
