package com.hhplus.concert_ticketing.infra.queue;

import com.hhplus.concert_ticketing.domain.queue.QueueRepository;
import com.hhplus.concert_ticketing.domain.queue.TokenEntity;
import com.hhplus.concert_ticketing.domain.queue.TokenStatus;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class QueueRepositoryImpl implements QueueRepository {
    private static final Logger logger = LoggerFactory.getLogger(QueueRepositoryImpl.class);
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
        logger.info("토큰={}의 대기 중인 토큰 수를 조회합니다. 상태={}", token, status);
        int count = queueJpaRepository.countWaitingTokensBefore(token, TokenStatus.WAITING);
        logger.info("토큰={}의 대기 중인 토큰 수 조회 결과: {}", token, count);
        return count;
    }

    @Override
    public List<TokenEntity> findTokensToExpire(TokenStatus status, Timestamp currentTime) {
        logger.info("만료될 토큰을 조회합니다. 상태={}, 현재 시간={}", status, currentTime);
        List<TokenEntity> tokens = queueJpaRepository.findTokensToExpire(TokenStatus.ACTIVE, currentTime);
        logger.info("만료될 토큰 조회 결과: {}개", tokens.size());
        return tokens;
    }

    @Override
    public List<TokenEntity> findTokensToActivate(Pageable pageable, TokenStatus status) {
        logger.info("활성화할 토큰을 조회합니다. 상태={}, 페이지={}", status, pageable);
        List<TokenEntity> tokens = queueJpaRepository.findTokensToActivate(pageable, status);
        logger.info("활성화할 토큰 조회 결과: {}개", tokens.size());
        return tokens;
    }

    @Override
    public long countByStatus(TokenStatus status) {
        logger.info("상태={}의 토큰 수를 조회합니다.", status);
        long count = queueJpaRepository.countByStatus(status);
        logger.info("상태={}의 토큰 수 조회 결과: {}", status, count);
        return count;
    }

    @Override
    public TokenEntity save(TokenEntity tokenEntity) {
        return queueJpaRepository.save(tokenEntity);
    }
}
