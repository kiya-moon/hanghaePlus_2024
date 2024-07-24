package com.hhplus.concert_ticketing.domain.queue;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Service
public class QueueService {

    private final QueueRepository queueRepository;

    public QueueService(QueueRepository queueRepository) {
        this.queueRepository = queueRepository;
    }

    public boolean isTokenIssued(Long userId) {
        return queueRepository.existsByUserId(userId);
    }

    // 토큰 생성
    public TokenEntity generateToken(Long userId) {
        if (queueRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("유효하지 않은 접근입니다.");
        }

        String token = UUID.randomUUID().toString();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Timestamp expiresAt = new Timestamp(now.getTime() + 5 * 60 * 1000);  // 5분 후

        TokenEntity tokenEntity = new TokenEntity(token, userId, now, expiresAt);

        return queueRepository.save(tokenEntity);
    }

    // 토큰 조회
    public TokenEntity checkToken(String token) {
        return queueRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 접근입니다."));
    }

    // 대기 중인 토큰 수 조회
    public int getWaitingCount(String token) {
        // 대기 중인 토큰의 수를 반환하는 로직을 구현합니다.
        // 예시: 대기열에서 나보다 앞에 있는 토큰의 수를 계산
        return queueRepository.countWaitingTokensBefore(token, TokenStatus.WAITING);
    }

    // 토큰 유효성 조회
    public boolean checkTokenValidity(String token) {
        TokenEntity tokenEntity = queueRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 접근입니다."));
        return tokenEntity.getStatus() == TokenStatus.ACTIVE;
    }

    // 토큰 만료 처리
    public void expireToken(String token) {
        TokenEntity tokenEntity = queueRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 접근입니다."));

        tokenEntity.expiredToken();
        queueRepository.save(tokenEntity);
    }

    // 토큰(s) 만료 처리
    @Transactional
    public void expireTokens() {
        List<TokenEntity> tokensToExpire = queueRepository.findTokensToExpire(TokenStatus.ACTIVE, new Timestamp(System.currentTimeMillis()));
        for (TokenEntity token : tokensToExpire) {
            token.expiredToken();
            queueRepository.save(token);
        }
    }

    // 토큰(s) 활성화 처리
    @Transactional
    public void activateTokens() {
        long activeCount = queueRepository.countByStatus(TokenStatus.ACTIVE);
        if (activeCount < 30) {
            // 필요한 토큰 수를 계산
            int tokensNeeded = 30 - (int) activeCount;

            // Pageable 객체를 생성하여 토큰의 수를 제한
            Pageable pageable = PageRequest.of(0, tokensNeeded);

            // 토큰을 조회
            List<TokenEntity> tokensToActivate = queueRepository.findTokensToActivate(pageable, TokenStatus.WAITING);

            // 토큰을 활성화하고 저장
            for (TokenEntity token : tokensToActivate) {
                token.activeToken();
                queueRepository.save(token);
            }
        }
    }
}
