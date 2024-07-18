package com.hhplus.concert_ticketing.infra.user;

import com.hhplus.concert_ticketing.domain.user.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
    @Query("SELECT u.balance FROM UserEntity u WHERE u.id = :userId")
    Optional<Double> findBalanceByUserId(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.balance = u.balance + :amount WHERE u.id = :userId AND u.version = :version")
    int chargePoint(@Param("userId") Long userId, @Param("amount") double amount, @Param("version") int version);

    @Modifying
    @Query("UPDATE UserEntity u SET u.balance = :balance, u.version = u.version + 1 WHERE u.id = :userId AND u.version = :version")
    int updateBalanceAndIncrementVersion(Long userId, Double balance, int version);
}
