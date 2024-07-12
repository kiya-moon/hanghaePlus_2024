package com.hhplus.concert_ticketing.domain.user;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @Query("SELECT u.balance FROM UserEntity u WHERE u.id = :userId")
    Optional<Double> findBalanceByUserId(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.balance = u.balance + :amount WHERE u.id = :userId AND u.version = :version")
    int chargePoint(@Param("userId") Long userId, @Param("amount") double amount, @Param("version") int version);

    Optional<UserEntity> findById(Long userId);

    UserEntity save(UserEntity user);
}
