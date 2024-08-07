package com.hhplus.concert_ticketing.domain.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    // 사용자 조회
    public User getUserInfo(Long userId) {
        Optional <User> userEntity = userRepository.findById(userId);
        if(userEntity.isEmpty()) {
            throw new IllegalStateException("사용자 정보가 없습니다.");
        }
        return userEntity.get();
    }

    // 포인트 충전
    // 사용자 개인 포인트이기 때문에 사용자만 접근 가능하긴 하나,
    // 두 개의 환경에서 접속하거나 사용자의 실수, 서버 문제 등의 경우를 대비해 낙관적락 적용
    // 또한 충전 결과를 검증하기 위해 새로운 balance 값을 다시 조회하여 검증
    @Transactional
    public int chargePoint(Long userId, int point) {
        // 낙관적락(기존 구현)
        // User user = getUserInfo(userId);
        // int balance = user.getBalance();
        // int chargeResult = balance + point;

        // // chargePoint 메서드 호출 시 낙관적 락을 위해 version 필드를 사용
        // int updatedRows = userRepository.chargePoint(userId, chargeResult, user.getVersion());
        // if (updatedRows == 0) {
        //     throw new RuntimeException("낙관적 락 예외. 다른 트랜잭션에서 엔티티가 수정되었습니다.");
        // }

        // // 새로운 balance 값을 다시 조회하여 검증
        // int newBalance = getUserInfo(userId).getBalance();
        // if (Math.abs(chargeResult - newBalance) > 0.01) {   // 소수점 오차 고려
        //     throw new RuntimeException("충전 결과 불일치. 예상 잔액: " + chargeResult + ", 실제 잔액: " + newBalance);
        // }

        // return newBalance;

        // 비관적락
        User user = userRepository.findByIdForUpdate(userId)
        .orElseThrow(() -> new IllegalStateException("사용자 정보가 없습니다."));

        int chargeResult = user.getBalance() + point;

        int updatedRows = userRepository.chargePoint(userId, chargeResult);
        if (updatedRows == 0) {
            throw new RuntimeException("포인트 충전 실패. 다른 트랜잭션에서 엔티티가 수정되었습니다.");
        }

        return getUserInfo(userId).getBalance();
    }


    // 포인트 사용
    @Transactional
    public int usePoint(Long userId, int price) {
        User user = userRepository.getUserInfo(userId);
        user.decreaseBalance(price);

        int updatedRows = userRepository.usePoint(userId, user.getBalance(), user.getVersion());
        if (updatedRows == 0) {
            throw new RuntimeException("낙관적 락 예외. 다른 트랜잭션에서 엔티티가 수정되었습니다.");
        }

        return getUserInfo(userId).getBalance();
    }
}
