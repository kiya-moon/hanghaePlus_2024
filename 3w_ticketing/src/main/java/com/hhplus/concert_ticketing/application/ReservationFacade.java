package com.hhplus.concert_ticketing.application;

import com.hhplus.concert_ticketing.domain.concert.ConcertService;
import com.hhplus.concert_ticketing.domain.concert.SeatEntity;
import com.hhplus.concert_ticketing.domain.concert.SeatRepository;
import com.hhplus.concert_ticketing.domain.concert.SeatStatus;
import com.hhplus.concert_ticketing.domain.queue.QueueService;
import com.hhplus.concert_ticketing.domain.queue.TokenStatus;
import com.hhplus.concert_ticketing.domain.reservation.ReservationEntity;
import com.hhplus.concert_ticketing.domain.reservation.ReservationRepository;
import com.hhplus.concert_ticketing.domain.reservation.ReservationService;
import com.hhplus.concert_ticketing.domain.user.UserEntity;
import com.hhplus.concert_ticketing.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

import static com.hhplus.concert_ticketing.domain.concert.SeatStatus.UNLOCKED;

@Service
@RequiredArgsConstructor
public class ReservationFacade {

    private final QueueService queueService;
    private final ConcertService concertService;
    private final ReservationService reservationService;
    private final UserService userService;

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;

    // 좌석 예약
    public void reserveSeat(String token, Long seatId, Long userId) {
        // 토큰 조회 > 유효x
        // 토큰 조회 > 사용자 조회 > 유효x
        // 토큰 조회 > 사용자 조회 > 좌석 상태 확인 > 이선좌
        // 토큰 조회 > 사용자 조회 > 좌석 상태 확인 > 좌석 예약 생성 > 토큰 만료

        // 토큰 유효성 확인
        boolean isTokenValid = queueService.checkToken(token).getStatus() == TokenStatus.ACTIVE;
        if (!isTokenValid) {
            throw new IllegalArgumentException("토큰이 만료되었습니다.");
        }

        // 사용자 조회
        UserEntity user = userService.getUserInfo(userId);

        // 좌석 상태 확인
        SeatEntity seatEntity = concertService.getSeatStatus(seatId);

        // 예약 생성
        reservationService.saveReservation(userId, seatId, seatEntity.getPrice());

        // 좌석 상태 변경
        seatEntity.lockSeat();

        // 토큰 만료
        queueService.expireToken(token);
    }

    // 예약 상태 관리
    public void manageReservationStatus() {
        Timestamp now = new Timestamp(System.currentTimeMillis());

        // 만료된 예약 상태를 "EXPIRED"로 변경하고 관련 좌석 상태를 "UNLOCKED"로 변경
        List<ReservationEntity> expiredReservations = reservationRepository.findByExpiresAtBeforeAndStatus(now, "ACTIVE");
        for (ReservationEntity reservation : expiredReservations) {
            reservation.expireReservation();
            reservationRepository.save(reservation);

            SeatEntity seat = seatRepository.findById(reservation.getSeatId()).orElse(null);
            if (seat != null) {
                seat.unlockSeat();
                seatRepository.save(seat);
            }
        }
    }
}
