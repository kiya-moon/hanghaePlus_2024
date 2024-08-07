package com.hhplus.concert_ticketing.infra.queue;

import com.hhplus.concert_ticketing.domain.queue.Token;
import com.hhplus.concert_ticketing.domain.queue.TokenStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface QueueJpaRepository extends JpaRepository<Token, Long> {
    boolean existsByUserId(Long userId);

    Optional<Token> findByToken(String token);

    @Query("SELECT COUNT(t) FROM Token t WHERE t.status = :status AND t.createdAt < (SELECT t2.createdAt FROM Token t2 WHERE t2.token = :token)")
    int countWaitingTokensBefore(@Param("token") String token, @Param("status") TokenStatus status);


    @Query("SELECT t FROM Token t WHERE t.status = :status AND t.expiresAt < :currentTime")
    List<Token> findTokensToExpire(@Param("status") TokenStatus status, @Param("currentTime") Timestamp currentTime);


    @Query("SELECT t FROM Token t WHERE t.status = :status ORDER BY t.createdAt ASC")
    List<Token> findTokensToActivate(Pageable pageable, @Param("status") TokenStatus status);


    @Query("SELECT COUNT(t) FROM Token t WHERE t.status = :status")
    long countByStatus(@Param("status") TokenStatus status);
}
