package com.hhplus.concert_ticketing.infra.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhplus.concert_ticketing.avro.Reservation;
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
    private String description; // 이벤트 설명

    @Column(name = "payload", columnDefinition = "JSON", nullable = false)
    private String payload; // 메세지 내용(직렬화로 저장)

    @Column(nullable = false)
    private String status = "INIT"; // 메세지 상태 : INIT, PUBLISHED, FAIL, RETRY

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime publishedAt;

    public static OutboxEvent create(String key, String domainType, String eventType, String description, String payload) {
        return OutboxEvent.builder()
                .key(key)
                .domainType(domainType)
                .eventType(eventType)
                .description(description)
                .payload(payload)
                .status("INIT")
                .createdAt(LocalDateTime.now())
                .build();
    }



    // 저장할 때 객체를 JSON으로 직렬화
    ObjectMapper objectMapper = new ObjectMapper();
    String jsonPayload;

    // 역직렬화
    com.hhplus.concert_ticketing.avro.Reservation abroObject;

    {
        try {
            jsonPayload = objectMapper.writeValueAsString(abroObject);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        try {
            abroObject = objectMapper.readValue(jsonPayload, Reservation.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
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
