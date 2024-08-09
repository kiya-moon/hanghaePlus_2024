package com.hhplus.concert_ticketing.presentation.config;

import com.hhplus.concert_ticketing.presentation.interceptor.TokenInterceptor;
import com.hhplus.concert_ticketing.presentation.interceptor.UserInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final TokenInterceptor tokenInterceptor;
    private final UserInterceptor userInterceptor;

    @Autowired
    public WebConfig(TokenInterceptor tokenInterceptor, UserInterceptor userInterceptor) {
        this.tokenInterceptor = tokenInterceptor;
        this.userInterceptor = userInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/api/reserve", "/api/concert"); // 적용할 API 경로를 지정

        registry.addInterceptor(userInterceptor)
                .addPathPatterns("/**"); // Adjust the path patterns as needed
    }
}

