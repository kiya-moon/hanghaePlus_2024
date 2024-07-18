package com.hhplus.concert_ticketing.infra.user;

import com.hhplus.concert_ticketing.domain.user.UserEntity;
import com.hhplus.concert_ticketing.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<Double> findBalanceByUserId(Long userId) {
        return userJpaRepository.findBalanceByUserId(userId);
    }

    @Override
    public int chargePoint(Long userId, double amount, int version) {
        return userJpaRepository.chargePoint(userId, amount, version);
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
        return userJpaRepository.updateBalanceAndIncrementVersion(userId, balance, version);
    }
}
