package com.hhplus.concert_ticketing.presentation.interceptor;

import com.hhplus.concert_ticketing.domain.queue.QueueService;
import com.hhplus.concert_ticketing.domain.queue.TokenStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(TokenInterceptor.class);
    private final QueueService queueService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");

        if (token == null || token.isEmpty()) {
            logger.warn("토큰이 제공되지 않았습니다.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "토큰이 필요합니다.");
            return false;
        }

        boolean isTokenValid = queueService.checkToken(token).getStatus() == TokenStatus.ACTIVE;
        if (!isTokenValid) {
            logger.warn("유효하지 않은 토큰: {}", token);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "토큰이 만료되었습니다.");
            return false;
        }

        return true;
    }
}
