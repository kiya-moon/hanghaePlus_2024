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
    public Double getBalance(Long userId) {
        logger.info("사용자 ID {}의 잔액 조회 시작", userId);
        try {
            UserEntity user = userService.getUserInfo(userId);
            Double balance = user.getBalance();
            logger.info("사용자 ID {}의 잔액 조회 성공: 잔액={}", userId, balance);
            return balance;
        } catch (Exception e) {
            logger.error("사용자 ID {}의 잔액 조회 실패: {}", userId, e.getMessage());
            throw e;
        }
    }

    // 사용자 포인트 충전
    public Double chargePoint(Long userId, Double amount) {
        logger.info("사용자 ID {}의 포인트 충전 시작: 충전액={}", userId, amount);
        try {
            Double newBalance = userService.chargePoint(userId, amount);
            logger.info("사용자 ID {}의 포인트 충전 성공: 새로운 잔액={}", userId, newBalance);
            return newBalance;
        } catch (Exception e) {
            logger.error("사용자 ID {}의 포인트 충전 실패: {}", userId, e.getMessage());
            throw e;
        }
    }
}
