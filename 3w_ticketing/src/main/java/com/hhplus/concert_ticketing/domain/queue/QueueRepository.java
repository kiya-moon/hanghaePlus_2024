package com.hhplus.concert_ticketing.domain.queue;

import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface QueueRepository {
    boolean existsByUserId(Long userId);

    TokenEntity save(TokenEntity tokenEntity);

    Optional<TokenEntity> findByToken(String token);

    long countByStatus(String activate);

    List<TokenEntity> findByStatus(String waiting);

    List<TokenEntity> findByExpiresAtBeforeAndStatus(Timestamp now, String waiting);
}
