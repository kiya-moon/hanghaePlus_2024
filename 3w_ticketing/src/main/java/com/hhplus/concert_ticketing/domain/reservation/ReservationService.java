package com.hhplus.concert_ticketing.domain.reservation;

import com.hhplus.concert_ticketing.domain.concert.SeatRepository;
import com.hhplus.concert_ticketing.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    // 예약 조회
    // 가져온 예약 정보를 통해 만료 여부도 알 수 있음
    public ReservationEntity getReservationInfo(Long reservationId) {
        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NoSuchElementException("예약이 존재하지 않습니다."));

        Timestamp now = new Timestamp(System.currentTimeMillis());

        if (reservation.getExpiresAt().before(now)) {
            throw new IllegalArgumentException("결제 시간이 만료되었습니다.");
        }

        return reservation;
    }

    // 예약 저장
    public void saveReservation(Long userId, Long seatId, int price) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Timestamp expiresAt = new Timestamp(now.getTime() + 5 * 60 * 1000);

        // 서비스단에서 ReservationEntity의 비즈니스 로직 호출
        ReservationEntity reservation = ReservationEntity.createReservation(userId, seatId, now, expiresAt, price);
        reservationRepository.save(reservation);
    }
}
