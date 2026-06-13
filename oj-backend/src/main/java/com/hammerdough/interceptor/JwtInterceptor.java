package com.hammerdough.interceptor;

import com.hammerdough.common.Result;
import com.hammerdough.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

// 交给Spring容器管理
@Component
public class JwtInterceptor implements HandlerInterceptor {

    // 容器自动注入
    @Resource
    private JwtUtil jwtUtil;

    // 读取yml配置，容器管理后该注解才生效
    @Value("${jwt.header}")
    private String tokenHeader;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader(tokenHeader);
        if (!StringUtils.hasText(token)) {
            return writeError(response, "请先登录");
        }

        try {
            Claims claims = jwtUtil.parseJWT(token);
            Integer userId = claims.get("userId", Integer.class);
            String username = claims.get("username", String.class);
            request.setAttribute("userId", userId);
            request.setAttribute("username", username);
        } catch (Exception e) {
            return writeError(response, "登录已失效，请重新登录");
        }
        return true;
    }

    private boolean writeError(HttpServletResponse response, String msg) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        Result<Object> result = Result.error(msg);
        response.getWriter().write(objectMapper.writeValueAsString(result));
        return false;
    }
}