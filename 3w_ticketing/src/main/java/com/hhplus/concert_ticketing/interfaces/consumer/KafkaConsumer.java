package com.hhplus.concert_ticketing.interfaces.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhplus.concert_ticketing.infra.event.OutboxEvent;
import com.hhplus.concert_ticketing.infra.event.OutboxEventRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public KafkaConsumer(OutboxEventRepository outboxEventRepository, ObjectMapper objectMapper) {
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
    }

    // Consumer 1: Outbox 상태 갱신
    @KafkaListener(topics = "PaidReservationTopic", groupId = "outbox-consumer-group")
    public void updateOutboxStatus(ConsumerRecord<String, String> record) {
        String eventId = record.key();
        String eventJson = record.value();
        try {
            Long id = Long.parseLong(eventId);
            OutboxEvent event = objectMapper.readValue(eventJson, OutboxEvent.class);
            outboxEventRepository.findById(id)
                    .ifPresent(existingEvent -> {
                        existingEvent.markAsPublished(); // Outbox 상태를 "PUBLISHED"로 갱신
                        outboxEventRepository.save(existingEvent);
                        logger.info("Outbox 상태 갱신 완료: 이벤트 ID {}", id);
                    });
        } catch (JsonProcessingException e) {
            logger.error("OutboxEvent JSON 변환 실패: 이벤트 ID {}", eventId, e);
        } catch (NumberFormatException e) {
            logger.error("이벤트 ID 변환 실패: {}", eventId, e);
        } catch (Exception e) {
            logger.error("Outbox 상태 갱신 실패: 이벤트 ID {}", eventId, e);
        }
    }

    // Consumer 2: 비즈니스 로직 수행
    @KafkaListener(topics = "PaidReservationTopic", groupId = "business-consumer-group")
    public void processBusinessLogic(ConsumerRecord<String, String> record) {
        String eventId = record.key();
        String eventJson = record.value();
        try {
            Long id = Long.parseLong(eventId);
            OutboxEvent event = objectMapper.readValue(eventJson, OutboxEvent.class);
            outboxEventRepository.findById(id)
                    .ifPresent(existingEvent -> {
                        if ("PUBLISHED".equals(existingEvent.getStatus())) {
                            // 비즈니스 로직 수행 (예: 알림 발송 등)
                            logger.info("예약 완료 후 알림 발송: 예약 ID {}", id);
                        }
                    });
        } catch (JsonProcessingException e) {
            logger.error("OutboxEvent JSON 변환 실패: 이벤트 ID {}", eventId, e);
        } catch (NumberFormatException e) {
            logger.error("이벤트 ID 변환 실패: {}", eventId, e);
        } catch (Exception e) {
            logger.error("비즈니스 로직 처리 실패: 예약 ID {}", eventId, e);
        }
    }
}
