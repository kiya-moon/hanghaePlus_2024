package com.hhplus.concert_ticketing.application;

import com.hhplus.concert_ticketing.domain.user.UserService;
import com.hhplus.concert_ticketing.domain.user.UserEntity;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserFacade {

    private static final Logger logger = LoggerFactory.getLogger(UserFacade.class);

    private final UserService userService;

    // 사용자 잔액 조회
    public int getBalance(Long userId) {
        try {
            UserEntity user = userService.getUserInfo(userId);
            return user.getBalance();
        } catch (Exception e) {
            logger.error("사용자 ID {}의 잔액 조회 실패: {}", userId, e.getMessage());
            throw e;
        }
    }

    // 사용자 포인트 충전
    public int chargePoint(Long userId, int amount) {
        try {
            return userService.chargePoint(userId, amount);
        } catch (Exception e) {
            logger.error("사용자 ID {}의 포인트 충전 실패: {}", userId, e.getMessage());
            throw e;
        }
    }
}
