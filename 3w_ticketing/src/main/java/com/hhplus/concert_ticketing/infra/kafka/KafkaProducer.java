package com.hhplus.concert_ticketing.infra.kafka;

import com.hhplus.concert_ticketing.avro.Reservation;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducer {

    private final KafkaTemplate<Long, Object> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<Long, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishPaidReservationInfo(Long reservationId, Reservation reservation) {
        kafkaTemplate.send(new ProducerRecord<>("paidReservationTopic", reservationId, reservation));
    }
}
