package com.hhplus.concert_ticketing.domain.user;

import java.util.Optional;

public interface UserRepository {
    Optional<Integer> findBalanceByUserId(Long userId);

    Optional<UserEntity> findByIdForUpdate(Long userId);

    int chargePoint(Long userId, int amount);

    Optional<UserEntity> findById(Long userId);

    UserEntity save(UserEntity user);

    int usePoint(Long userId, int balance, int version);

    UserEntity getUserInfo(Long userId);
}
