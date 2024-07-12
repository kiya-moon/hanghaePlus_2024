package com.hhplus.concert_ticketing.domain.queue;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.sql.Timestamp;

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

    @Builder
    public TokenEntity(Long id, Long userId, String token, TokenStatus status, Timestamp createdAt, Timestamp expiresAt) {
        this.id = id;
        this.userId = userId;
        this.token = token;
        this.status = status;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }
}
