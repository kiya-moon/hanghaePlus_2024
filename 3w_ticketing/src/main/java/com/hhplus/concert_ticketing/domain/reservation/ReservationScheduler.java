package com.hhplus.concert_ticketing.domain.reservation;

import com.hhplus.concert_ticketing.domain.concert.SeatEntity;
import com.hhplus.concert_ticketing.domain.concert.SeatRepository;
import com.hhplus.concert_ticketing.presentation.concert.Seat;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;

import static com.hhplus.concert_ticketing.domain.concert.SeatStatus.UNLOCKED;

@Component
@RequiredArgsConstructor
public class ReservationScheduler {
    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;

    @Scheduled(cron = "0 * * * * *")  // 매분 정각에 실행
    public void manageReservations() {
        Timestamp now = new Timestamp(System.currentTimeMillis());

        // 만료된 예약 상태를 "EXPIRED"로 변경
        List<ReservationEntity> expiredReservations = reservationRepository.findByExpiresAtBeforeAndStatus(now, "ACTIVE");
        for (ReservationEntity reservation : expiredReservations) {
            reservation.setStatus("EXPIRED");
            reservationRepository.save(reservation);

            // 관련 좌석 상태를 "UNLOCKED"로 변경
            SeatEntity seat = seatRepository.findById(reservation.getSeatId()).orElse(null);
            if (seat != null) {
                seat.setStatus(UNLOCKED);
                seatRepository.save(seat);
            }
        }
    }
}

