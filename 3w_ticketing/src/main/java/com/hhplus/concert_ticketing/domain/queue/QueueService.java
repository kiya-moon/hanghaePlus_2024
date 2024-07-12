package com.hhplus.concert_ticketing.domain.queue;

import com.hhplus.concert_ticketing.presentation.queue.TokenData;
import com.hhplus.concert_ticketing.presentation.queue.TokenResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;
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

    public TokenEntity generateToken(Long userId) {
        if (queueRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("유효하지 않은 접근입니다.");
        }

        String token = UUID.randomUUID().toString();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Timestamp expiresAt = new Timestamp(now.getTime() + 5 * 60 * 1000);  // 5분 후

        TokenEntity tokenEntity = new TokenEntity();
        tokenEntity.setToken(token);
        tokenEntity.setUserId(userId);
        tokenEntity.setStatus(TokenStatus.WAITING);
        tokenEntity.setCreatedAt(now);
        tokenEntity.setExpiresAt(expiresAt);

        return queueRepository.save(tokenEntity);
    }

    public ResponseEntity<TokenResponse> checkToken(String token) {
        TokenEntity tokenEntity = queueRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 접근입니다."));

        TokenStatus status = tokenEntity.getStatus();
        if (status == TokenStatus.ACTIVE) {
            return ResponseEntity.status(HttpStatus.SEE_OTHER)
                    .header(HttpHeaders.LOCATION, "/reservation-page")
                    .body(new TokenResponse("200", "예약 페이지로 이동합니다.", new TokenData(token, 1, tokenEntity.getExpiresAt().toString())));
        } else {
            return ResponseEntity.status(HttpStatus.CONTINUE)
                    .body(new TokenResponse("100", "계속 대기합니다.", new TokenData(token, 1, tokenEntity.getExpiresAt().toString())));
        }
    }

    public boolean checkTokenValidity(String token) {
        TokenEntity tokenEntity = queueRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 접근입니다."));
        return tokenEntity.getStatus() == TokenStatus.ACTIVE;
    }

    public void expireToken(String token) {
        TokenEntity tokenEntity = queueRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 접근입니다."));

        tokenEntity.setStatus(TokenStatus.EXPIRED);
        queueRepository.save(tokenEntity);
    }
}
