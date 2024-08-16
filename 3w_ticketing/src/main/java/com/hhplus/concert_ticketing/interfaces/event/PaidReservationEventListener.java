package com.hhplus.concert_ticketing.interfaces.event;

import com.hhplus.concert_ticketing.domain.event.PaidReservationEvent;
import com.hhplus.concert_ticketing.infra.kafka.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class PaidReservationEventListener {
    private static final Logger logger = LoggerFactory.getLogger(ReservationEventListener.class);
    private final KafkaProducer kafkaProducer;

    public PaidReservationEventListener(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendPaidReservationInfo(PaidReservationEvent event) {
        kafkaProducer.publishPaidReservationInfo(event.getReservationId(), event.getReservation());
    }
}
