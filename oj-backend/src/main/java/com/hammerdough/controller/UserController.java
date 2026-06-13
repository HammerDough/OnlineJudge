package com.hammerdough.controller;


import com.hammerdough.common.Result;
import com.hammerdough.dto.UserDTO;
import com.hammerdough.dto.UserUpdateDTO;
import com.hammerdough.entity.User;
import com.hammerdough.service.UserService;
import com.hammerdough.util.JwtUtil;
import com.hammerdough.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;

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

    @PostMapping("/login")
    public Result<String> login(@RequestBody UserDTO userDTO) {
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();

        if(username ==null || "".equals(username.trim())){
            return Result.error("账号不能为空");
        }
        if(password ==null || "".equals(password.trim())){
            return Result.error("密码不能为空");
        }


        User user = userService.login(userDTO);
        if(user == null){
            return Result.error("账号或密码错误");
        }

        Map<String,Object> claims = new HashMap<>();
        claims.put("userId",user.getUserId());
        claims.put("username",username);

        String token = jwtUtil.createJWT(claims);
        return Result.success(token);
    }

    @GetMapping("/info")
    public Result<UserVO> getUserInfo(HttpServletRequest request){
        Integer userId = (Integer)request.getAttribute("userId");
        if(userId == null){
            return Result.error("未登录");
        }

        User user = userService.getById(userId);
        if(user == null){
            return Result.error("用户不存在");
        }

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user,userVO);

        return Result.success(userVO);
    }

    @GetMapping("info/{userId}")
    public Result<UserVO> getUserInfo(@PathVariable Integer userId){
        if(userId == null || userId <= 0){
            return Result.error("用户非法ID");
        }

        User user = userService.getById(userId);
        if(user == null){
            return Result.error("用户不存在");
        }

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user,userVO);

        return Result.success(userVO);
    }

    @PutMapping("/update")
    public Result<UserVO> updateUserInfo(HttpServletRequest request,
                         @RequestBody UserUpdateDTO dto){
        Integer loginUserId = (Integer) request.getAttribute("userId");
        if(loginUserId == null){
            return Result.error("未登录");
        }

        User user = userService.updateUserInfo(loginUserId,dto);
        if(user == null){
            return Result.error("用户不存在");
        }

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user,userVO);
        return Result.success(userVO);
    }



}
