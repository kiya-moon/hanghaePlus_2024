package com.hhplus.concert_ticketing.application;

import com.hhplus.concert_ticketing.domain.reservation.Reservation;
import com.hhplus.concert_ticketing.infra.external.DataPlatformMockApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class ReservationEventListener {
    private static final Logger logger = LoggerFactory.getLogger(ReservationEventListener.class);
    private final DataPlatformMockApiClient dataPlatformMockApiClient;

    public ReservationEventListener(DataPlatformMockApiClient dataPlatformMockApiClient) {
        this.dataPlatformMockApiClient = dataPlatformMockApiClient;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendReservationInfo(PaidEvent paidEvent) {
        Reservation reservation = paidEvent.getReservation();
        try {
            dataPlatformMockApiClient.sendReservationInfo(reservation);
        } catch (Exception e) {
            logger.error("예약 정보 외부 전송 실패: {}", e.getMessage());
        }
    }
}
