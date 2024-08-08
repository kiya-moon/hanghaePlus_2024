package com.hhplus.concert_ticketing.domain.user;

import java.util.Optional;

public interface UserRepository {
    Optional<Integer> findBalanceByUserId(Long userId);

    Optional<User> findByIdForUpdate(Long userId);

    int chargePoint(Long userId, int amount);

    Optional<User> findById(Long userId);

    User save(User user);

    int usePoint(Long userId, int balance, int version);

    User getUserInfo(Long userId);
}
