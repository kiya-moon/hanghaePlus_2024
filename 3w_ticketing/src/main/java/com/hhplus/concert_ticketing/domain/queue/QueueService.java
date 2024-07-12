package com.hhplus.concert_ticketing.domain.queue;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.UUID;

import static com.hhplus.concert_ticketing.domain.queue.TokenStatus.*;

@Service
@RequiredArgsConstructor
public class QueueService {
    private final QueueRepository queueRepository;

    // 토큰 발급 여부 조회
    public boolean isTokenIssued(Long userId) {
        return queueRepository.existsByUserId(userId);  // 유저 ID로 토큰이 있는지 확인
    }

    // 토큰 생성 메서드
    // random UUID로 토큰 생성
    // 토큰테이블에 데이터가 30개 미만이라면 상태는 활성('ACTIVATE')으로 저장, 아니라면 대기('WAITING')로 저장
    // created_at은 현재 시간으로 저장
    // expires_at은 현재 시간에 5분 플러스 해서 저장
    public TokenEntity generateToken(Long userId) {
        if (isTokenIssued(userId)) {
            throw new IllegalStateException("유저 ID로 이미 발급된 토큰이 있습니다.");
            // 유저 ID로 이미 발급된 토큰이 있는 경우 어떻게 처리할지 조금 더 고민 중
        }

        UUID tokenUUID = UUID.randomUUID();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Timestamp expiresAt = new Timestamp(now.getTime() + 5 * 60 * 1000);  // 5분 후

        TokenEntity tokenEntity = new TokenEntity();
        tokenEntity.setToken(tokenUUID.toString());
        tokenEntity.setUserId(userId);  // 유저 ID 설정
        tokenEntity.setCreatedAt(now);
        tokenEntity.setExpiresAt(expiresAt);
        tokenEntity.setStatus(WAITING);  // 새로운 토큰은 항상 웨이팅 상태로 생성

        return queueRepository.save(tokenEntity);
    }

    // 대기열 조회 메서드
    public TokenStatus checkToken(String token) {
        TokenEntity tokenEntity = queueRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 접근입니다."));

        return TokenStatus.valueOf(String.valueOf(tokenEntity.getStatus()));  // Enum으로 변환하여 반환
    }

    // 토큰 유효성 확인 메서드
    public boolean checkTokenValidity(String token) {
        TokenEntity tokenEntity = queueRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 접근입니다."));

        return ACTIVE.equals(tokenEntity.getStatus());
    }

    // 토큰 만료시키는 메서드
    public void expireToken(String token) {
        TokenEntity tokenEntity = queueRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 토큰입니다."));
        tokenEntity.setStatus(EXPIRED);
        queueRepository.save(tokenEntity);
    }
}
