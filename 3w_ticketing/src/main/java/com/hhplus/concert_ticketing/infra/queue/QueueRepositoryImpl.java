package com.hhplus.concert_ticketing.infra.queue;

import com.hhplus.concert_ticketing.domain.queue.QueueRepository;
import com.hhplus.concert_ticketing.domain.queue.TokenEntity;
import com.hhplus.concert_ticketing.domain.queue.TokenStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class QueueRepositoryImpl implements QueueRepository {
    private final QueueJpaRepository queueJpaRepository;

    @Override
    public boolean existsByUserId(Long userId) {
        return queueJpaRepository.existsByUserId(userId);
    }

    @Override
    public Optional<TokenEntity> findByToken(String token) {
        return queueJpaRepository.findByToken(token);
    }

    @Override
    public int countWaitingTokensBefore(String token, TokenStatus status) {
        return queueJpaRepository.countWaitingTokensBefore(token, TokenStatus.WAITING);
    }

    @Override
    public List<TokenEntity> findTokensToExpire(TokenStatus status, Timestamp currentTime) {
        return queueJpaRepository.findTokensToExpire(TokenStatus.ACTIVE, currentTime);
    }

    @Override
    public List<TokenEntity> findTokensToActivate(long limit) {
        return queueJpaRepository.findTokensToActivate(limit);
    }

    @Override
    public long countByStatus(TokenStatus status) {
        return queueJpaRepository.countByStatus(status);
    }

    @Override
    public TokenEntity save(TokenEntity tokenEntity) {
        return queueJpaRepository.save(tokenEntity);
    }
}
