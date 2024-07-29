package com.hhplus.concert_ticketing.domain.queue;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.sql.Timestamp;

import static com.hhplus.concert_ticketing.domain.concert.SeatStatus.UNLOCKED;
import static com.hhplus.concert_ticketing.domain.queue.TokenStatus.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String token;
    private TokenStatus status;
    private Timestamp createdAt;
    private Timestamp expiresAt;

    public TokenEntity(String token, Long userId, Timestamp createdAt, Timestamp expiresAt) {
        this.token = token;
        this.userId = userId;
        this.status = WAITING;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public TokenEntity(String token, Long userId, TokenStatus status, Timestamp createdAt, Timestamp expiresAt) {
    }

    public static TokenEntity createToken(String token, Long userId, TokenStatus status, Timestamp createdAt, Timestamp expiresAt) {
        return new TokenEntity(token, userId, status, createdAt, expiresAt);
    }

    public void activeToken() {
        this.setStatus(ACTIVE);
    }
    public void waitingToken() {
        this.setStatus(WAITING);
    }
    public void expiredToken() {
        this.setStatus(EXPIRED);
    }
}
