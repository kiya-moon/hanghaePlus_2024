package com.hhplus.concert_ticketing.domain.queue;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface QueueRepository extends JpaRepository<TokenEntity, Long> {
    boolean existsByUserId(Long userId);

    TokenEntity save(TokenEntity tokenEntity);

    Optional<TokenEntity> findByToken(String token);

    long countByStatus(String activate);

    List<TokenEntity> findByStatus(String waiting);

    List<TokenEntity> findByExpiresAtBeforeAndStatus(Timestamp now, String waiting);
}
