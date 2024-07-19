package com.hhplus.concert_ticketing.presentation.reservation;

import com.hhplus.concert_ticketing.application.ReservationFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // SLF4J 로깅 라이브러리
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationScheduler {
    private final ReservationFacade reservationFacade;

    @Scheduled(cron = "0 * * * * *")  // 매분 정각에 실행
    public void manageReservationStatus() {
        log.info("예약 상태 관리 작업을 {}에 시작합니다.", System.currentTimeMillis());
        try {
            reservationFacade.manageReservationStatus();
            log.info("예약 상태 관리 작업이 {}에 성공적으로 완료되었습니다.", System.currentTimeMillis());
        } catch (Exception e) {
            log.error("예약 상태 관리 작업 중 오류가 발생했습니다.", e);
        }
    }
}
