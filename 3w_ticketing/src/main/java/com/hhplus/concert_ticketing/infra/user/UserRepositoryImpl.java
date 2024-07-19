package com.hhplus.concert_ticketing.infra.user;

import com.hhplus.concert_ticketing.domain.user.UserEntity;
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
    public Optional<Double> findBalanceByUserId(Long userId) {
        logger.info("사용자ID={}의 잔액을 조회합니다.", userId);
        Optional<Double> balance = userJpaRepository.findBalanceByUserId(userId);
        if (balance.isPresent()) {
            logger.info("사용자ID={}의 잔액 조회 결과: {}", userId, balance.get());
        } else {
            logger.info("사용자ID={}의 잔액 조회 결과: 없음", userId);
        }
        return balance;
    }

    @Override
    public int chargePoint(Long userId, double amount, int version) {
        logger.info("사용자ID={}에 포인트 충전 요청: 충전액={}, 버전={}", userId, amount, version);
        try {
            int updatedRows = userJpaRepository.chargePoint(userId, amount, version);
            logger.info("사용자ID={}에 포인트 충전 완료: 충전액={}, 업데이트된 행 수={}", userId, amount, updatedRows);
            return updatedRows;
        } catch (Exception e) {
            logger.error("사용자ID={}에 포인트 충전 실패: 충전액={}, 버전={}, 에러={}", userId, amount, version, e.getMessage());
            throw e;
        }
    }

    @Override
    public Optional<UserEntity> findById(Long userId) {
        return userJpaRepository.findById(userId);
    }

    @Override
    public UserEntity save(UserEntity user) {
        return userJpaRepository.save(user);
    }

    @Override
    public int updateBalanceAndIncrementVersion(Long userId, Double balance, int version) {
        logger.info("사용자ID={}의 잔액을 업데이트하고 버전을 증가시킵니다: 새 잔액={}, 새 버전={}", userId, balance, version);
        try {
            int updatedRows = userJpaRepository.updateBalanceAndIncrementVersion(userId, balance, version);
            logger.info("사용자ID={}의 잔액 업데이트 및 버전 증가 완료: 새 잔액={}, 새 버전={}, 업데이트된 행 수={}", userId, balance, version, updatedRows);
            return updatedRows;
        } catch (Exception e) {
            logger.error("사용자ID={}의 잔액 업데이트 및 버전 증가 실패: 새 잔액={}, 새 버전={}, 에러={}", userId, balance, version, e.getMessage());
            throw e;
        }
    }
}
