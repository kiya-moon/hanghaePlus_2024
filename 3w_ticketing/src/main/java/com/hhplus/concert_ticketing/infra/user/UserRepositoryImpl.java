package com.hhplus.concert_ticketing.infra.user;

import com.hhplus.concert_ticketing.domain.user.User;
import com.hhplus.concert_ticketing.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private static final Logger logger = LoggerFactory.getLogger(UserRepositoryImpl.class);
    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<Integer> findBalanceByUserId(Long userId) {
        logger.info("사용자ID={}의 잔액을 조회합니다.", userId);
        Optional<Integer> balance = userJpaRepository.findBalanceByUserId(userId);
        if (balance.isPresent()) {
            logger.info("사용자ID={}의 잔액 조회 결과: {}", userId, balance.get());
        } else {
            logger.info("사용자ID={}의 잔액 조회 결과: 없음", userId);
        }
        return balance;
    }

    @Override
    public Optional<User> findByIdForUpdate(Long userId) {
        return userJpaRepository.findByIdForUpdate(userId);
    }

    @Override
    public int chargePoint(Long userId, int amount) {
        logger.info("사용자ID={}에 포인트 충전 요청: 충전액={}", userId, amount);
        try {
            int updatedRows = userJpaRepository.chargePoint(userId, amount);
            logger.info("사용자ID={}에 포인트 충전 완료: 충전액={}, 업데이트된 행 수={}", userId, amount, updatedRows);
            return updatedRows;
        } catch (Exception e) {
            logger.error("사용자ID={}에 포인트 충전 실패: 충전액={}, 에러={}", userId, amount, e.getMessage());
            throw e;
        }
    }

    @Override
    public Optional<User> findById(Long userId) {
        return userJpaRepository.findById(userId);
    }

    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }

    @Override
    public int usePoint(Long userId, int balance, int version) {
        logger.info("사용자ID={}의 잔액을 업데이트 요청: 새 잔액={}, 버전={}", userId, balance, version);
        try {
            int updatedRows = userJpaRepository.usePoint(userId, balance, version);
            logger.info("사용자ID={}의 잔액 업데이트 완료: 새 잔액={}, 버전={}, 업데이트된 행 수={}", userId, balance, version, updatedRows);
            return updatedRows;
        } catch (Exception e) {
            logger.error("사용자ID={}의 잔액 업데이트 실패: 새 잔액={}, 버전={}, 에러={}", userId, balance, version, e.getMessage());
            throw e;
        }
    }

    @Override
    public User getUserInfo(Long userId) {
        return userJpaRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("사용자 정보가 없습니다."));
    }

}
