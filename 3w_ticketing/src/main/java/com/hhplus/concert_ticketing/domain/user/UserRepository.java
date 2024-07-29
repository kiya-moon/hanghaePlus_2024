package com.hhplus.concert_ticketing.domain.user;

import java.util.Optional;

public interface UserRepository {
    Optional<Double> findBalanceByUserId(Long userId);

    Optional<UserEntity> findByIdForUpdate(Long userId);

    int chargePoint(Long userId, double amount);

    Optional<UserEntity> findById(Long userId);

    UserEntity save(UserEntity user);

    int usePoint(Long userId, Double balance, int version);

    UserEntity getUserInfo(Long userId);
}
