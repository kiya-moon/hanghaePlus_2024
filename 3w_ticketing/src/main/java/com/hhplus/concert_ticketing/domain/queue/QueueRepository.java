package com.hhplus.concert_ticketing.domain.queue;

import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface QueueRepository {
    boolean existsByUserId(Long userId);

    Optional<TokenEntity> findByToken(String token);

    int countWaitingTokensBefore(String token, TokenStatus status);

    List<TokenEntity> findTokensToExpire(TokenStatus status, Timestamp currentTime);

    List<TokenEntity> findTokensToActivate(Pageable pageable, TokenStatus status);

    long countByStatus(TokenStatus status);

    TokenEntity save(TokenEntity tokenEntity);
}
