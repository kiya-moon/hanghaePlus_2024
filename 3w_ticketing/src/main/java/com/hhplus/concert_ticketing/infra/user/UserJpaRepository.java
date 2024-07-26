package com.hhplus.concert_ticketing.infra.user;

import com.hhplus.concert_ticketing.domain.user.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
    // 충전 - 낙관적락(기존 구현)
    // @Query("SELECT u.balance FROM UserEntity u WHERE u.id = :userId")
    // Optional<Double> findBalanceByUserId(@Param("userId") Long userId);

    // @Modifying
    // @Transactional
    // @Query("UPDATE UserEntity u SET u.balance = u.balance + :amount WHERE u.id = :userId AND u.version = :version")
    // int chargePoint(@Param("userId") Long userId, @Param("amount") double amount, @Param("version") int version);

    // 충전 - 비관적락
    @Query("SELECT u FROM UserEntity u WHERE u.id = :userId")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<UserEntity> findByIdForUpdate(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.balance = u.balance + :amount WHERE u.id = :userId")
    int chargePoint(@Param("userId") Long userId, @Param("amount") double amount);
    
    @Modifying
    @Query("UPDATE UserEntity u SET u.balance = :balance WHERE u.id = :userId AND u.version = :version")
    int usePoint(@Param("userId") Long userId, @Param("balance") Double balance, @Param("version") int version);
}
