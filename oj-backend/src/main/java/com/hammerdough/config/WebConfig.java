package com.hammerdough.config;

import com.hammerdough.interceptor.JwtInterceptor;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;



@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 直接注入容器中的拦截器Bean
    @Resource
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/user/register",
                        "/user/login",
                        "/user/info/*",
                        "/upload/avatar/**",
                        "/problem/**",
                        "/favicon.ico",
                        "/error",
                        "/static/**",
                        "/images/**",
                        "/js/**",
                        "/css/**"
                );
    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/avatar/**")
                .addResourceLocations("file:D:/GitHubProjects/OnlineJudge/oj-upload/avatar");
    }
}