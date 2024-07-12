package com.hhplus.concert_ticketing.application;

import com.hhplus.concert_ticketing.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserFacade {
    private final UserService userService;

    // 사용자 잔액 조회
    public Double getBalance(Long userId) {
        return userService.getBalance(userId);
    }

    // 사용자 포인트 충전
    public Double chargePoint(Long userId, Double amount) {
        return userService.chargePoint(userId, amount);
    }
}
