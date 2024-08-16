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
    private static final Logger logger = LoggerFactory.getLogger(PaidReservationEventListener.class);
    private final KafkaProducer kafkaProducer;

    public PaidReservationEventListener(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendPaidReservationInfo(PaidReservationEvent event) {
        try {
            // 이벤트 처리 로그 추가
            logger.info("결제 완료 이벤트 처리 중: 예약 ID {}", event.getReservationId());
            // Kafka 메시지 전송
            kafkaProducer.publishPaidReservationInfo(event.getReservationId(), event.getReservation());
        } catch (Exception e) {
            // 예외 발생 시 로그 출력
            logger.error("예약 정보 전송 실패: 예약 ID {}", event.getReservationId(), e);
        }
    }
}
