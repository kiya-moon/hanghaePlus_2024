package com.hhplus.concert_ticketing.presentation.reservation;

import com.hhplus.concert_ticketing.application.ReservationFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationScheduler {
    private final ReservationFacade reservationFacade;

    @Scheduled(cron = "0 * * * * *")  // 매분 정각에 실행
    public void manageReservationStatus() {
        reservationFacade.manageReservationStatus();
    }
}

