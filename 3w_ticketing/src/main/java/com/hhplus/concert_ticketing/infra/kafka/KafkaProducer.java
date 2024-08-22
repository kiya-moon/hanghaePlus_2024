package com.hhplus.concert_ticketing.infra.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishPaidReservationInfo(String reservationId, String reservation) {
        try {
            // Reservation 객체를 JSON 문자열로 변환
            String reservationJson = objectMapper.writeValueAsString(reservation);
            // Kafka 메시지로 전송
            kafkaTemplate.send(new ProducerRecord<>("paidReservationTopic", reservationId.toString(), reservationJson));
        } catch (JsonProcessingException e) {
            // JSON 변환 실패 처리
            throw new RuntimeException("JSON 변환 실패", e);
        }
    }
}
