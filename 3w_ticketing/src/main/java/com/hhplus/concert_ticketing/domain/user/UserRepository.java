package com.hhplus.concert_ticketing.domain.user;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository {
    Optional<Double> getBalance(Long userId);

    int chargePoint(Long userId, double v, int version);

    @Modifying
    Optional<UserEntity> findById(Long userId);

    void save(UserEntity user);
}
