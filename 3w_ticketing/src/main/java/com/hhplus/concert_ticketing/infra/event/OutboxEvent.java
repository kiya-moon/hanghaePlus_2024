package com.hhplus.concert_ticketing.infra.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "outbox_event")
public class OutboxEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    // 이벤트 ID

    private String key; // 메세지 키(직렬화)

    @Column(nullable = false)
    private String domainType;  // 도메인 타입 : user, concert, queue, reservation, payment

    @Column(nullable = false)
    private String eventType;   // 이벤트 타입 : paidEvent, ...

    @Column(nullable = false)
    private String payload; // 메세지 내용(직렬화로 저장)

    @Column(nullable = false)
    private String status = "INIT"; // 메세지 상태 : INIT, PUBLISHED, FAIL, RETRY

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime publishedAt;

    public static OutboxEvent create(String key, String domainType, String eventType, String payload) {
        return OutboxEvent.builder()
                .key(key)
                .domainType(domainType)
                .eventType(eventType)
                .payload(payload)
                .status("INIT")
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void markAsPublished() {
        this.status = "PUBLISHED";
        this.publishedAt = LocalDateTime.now();
    }

    public void markAsFailed() {
        this.status = "FAIL";
    }

    public void markAsRetrying() {
        this.status = "RETRY";
    }
}
