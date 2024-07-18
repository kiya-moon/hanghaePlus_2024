package com.hhplus.concert_ticketing.domain.queue;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface QueueRepository {
    boolean existsByUserId(Long userId);

    Optional<TokenEntity> findByToken(String token);

    int countWaitingTokensBefore(String token, TokenStatus status);

    List<TokenEntity> findTokensToExpire(TokenStatus status, Timestamp currentTime);

    List<TokenEntity> findTokensToActivate(long limit);

    long countByStatus(TokenStatus status);

    TokenEntity save(TokenEntity tokenEntity);
}
