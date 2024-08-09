package com.hhplus.concert_ticketing.presentation.interceptor;

import com.hhplus.concert_ticketing.domain.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class UserInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(UserInterceptor.class);
    private final UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userIdStr = request.getHeader("userId");
        if (userIdStr == null) {
            logger.error("사용자 ID 헤더가 없습니다.");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "사용자 ID 헤더가 없습니다.");
            return false;
        }

        Long userId;
        try {
            userId = Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            logger.error("잘못된 사용자 ID 형식: {}", userIdStr);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "잘못�� 사용자 ID 형식입니다.");
            return false;
        }

        try {
            userService.getUserInfo(userId);
        } catch (Exception e) {
            logger.error("사용자 ID {} 조회 실패: {}", userId, e.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 사용자입니다.");
            return false;
        }

        return true;
    }
}
