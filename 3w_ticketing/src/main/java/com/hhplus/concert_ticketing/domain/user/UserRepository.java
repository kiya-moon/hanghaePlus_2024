package com.hhplus.concert_ticketing.domain.user;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface UserRepository {
    Optional<Double> findBalanceByUserId(Long userId);

    Optional<UserEntity> findByIdForUpdate(Long userId);

    int chargePoint(Long userId, double amount, int version);

    Optional<UserEntity> findById(Long userId);

    UserEntity save(UserEntity user);

    int usePoint(Long userId, Double balance, int version);
}
