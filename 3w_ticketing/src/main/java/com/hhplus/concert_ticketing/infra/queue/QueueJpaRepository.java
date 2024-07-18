package com.hhplus.concert_ticketing.infra.queue;

import com.hhplus.concert_ticketing.domain.queue.TokenEntity;
import com.hhplus.concert_ticketing.domain.queue.TokenStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface QueueJpaRepository extends JpaRepository<TokenEntity, Long> {
    boolean existsByUserId(Long userId);

    Optional<TokenEntity> findByToken(String token);

    @Query("SELECT COUNT(t) FROM TokenEntity t WHERE t.status = :status AND t.createdAt < (SELECT t2.createdAt FROM TokenEntity t2 WHERE t2.token = :token)")
    int countWaitingTokensBefore(@Param("token") String token, @Param("status") TokenStatus status);


    @Query("SELECT t FROM TokenEntity t WHERE t.status = :status AND t.expiresAt < :currentTime")
    List<TokenEntity> findTokensToExpire(@Param("status") TokenStatus status, @Param("currentTime") Timestamp currentTime);


    @Query(value = "SELECT t FROM TokenEntity t WHERE t.status = 'WAITING' ORDER BY t.createdAt ASC LIMIT :limit", nativeQuery = true)
    List<TokenEntity> findTokensToActivate(@Param("limit") long limit);

    @Query("SELECT COUNT(t) FROM TokenEntity t WHERE t.status = :status")
    long countByStatus(@Param("status") TokenStatus status);
}
