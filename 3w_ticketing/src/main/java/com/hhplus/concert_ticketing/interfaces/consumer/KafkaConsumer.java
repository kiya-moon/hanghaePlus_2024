package com.hhplus.concert_ticketing.interfaces.consumer;

import com.hhplus.concert_ticketing.domain.event.PaidReservationMessage;
import com.hhplus.concert_ticketing.infra.event.OutboxEvent;
import com.hhplus.concert_ticketing.infra.event.OutboxEventRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static org.springframework.data.repository.util.ClassUtils.ifPresent;

@Component
public class KafkaConsumer {
    private final OutboxEventRepository outboxEventRepository;

    public KafkaConsumer(OutboxEventRepository outboxEventRepository) {
        this.outboxEventRepository = outboxEventRepository;
    }

    // Consumer 1: Outbox 상태 갱신
    @KafkaListener(topics = "PaidReservationTopic", groupId = "outbox-consumer-group")
    public void updateOutboxStatus(ConsumerRecord<Long, Object> record) {
        Long eventId = record.key();
        outboxEventRepository.findById(eventId)
                .ifPresent(event -> {
                    event.markAsPublished(); // Outbox 상태를 "PUBLISHED"로 갱신
                    outboxEventRepository.save(event);
                });
    }

    // Consumer 2: 비즈니스 로직 수행
    @KafkaListener(topics = "PaidReservationTopic", groupId = "business-consumer-group")
    public void processBusinessLogic(ConsumerRecord<Long, Object> record)  {
        Long eventId = record.key();
        outboxEventRepository.findById(eventId)
                .ifPresent(event -> {
                    if ("PUBLISHED".equals(event.getStatus())) {
                        // 비즈니스 로직 수행 (예: 알림 발송 등)
                    }
                });
    }
}
