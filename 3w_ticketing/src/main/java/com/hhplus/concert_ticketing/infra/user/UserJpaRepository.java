package com.hhplus.concert_ticketing.infra.user;

import com.hhplus.concert_ticketing.domain.user.UserEntity;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
     @Query("SELECT u.balance FROM UserEntity u WHERE u.id = :userId")
     Optional<Integer> findBalanceByUserId(@Param("userId") Long userId);

    // 충전 - 낙관적락(기존 구현)
    // @Modifying
    // @Transactional
    // @Query("UPDATE UserEntity u SET u.balance = u.balance + :amount WHERE u.id = :userId AND u.version = :version")
    // int chargePoint(@Param("userId") Long userId, @Param("amount") int amount, @Param("version") int version);

    // 충전 - 비관적락
    @Query("SELECT u FROM UserEntity u WHERE u.id = :userId")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<UserEntity> findByIdForUpdate(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.balance = u.balance + :amount WHERE u.id = :userId")
    int chargePoint(@Param("userId") Long userId, @Param("amount") int amount);
    
    @Modifying
    @Query("UPDATE UserEntity u SET u.balance = :balance WHERE u.id = :userId AND u.version = :version")
    int usePoint(@Param("userId") Long userId, @Param("balance") int balance, @Param("version") int version);

}
