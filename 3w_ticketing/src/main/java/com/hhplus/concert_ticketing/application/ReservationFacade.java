package com.hhplus.concert_ticketing.application;

import com.hhplus.concert_ticketing.domain.concert.ConcertService;
import com.hhplus.concert_ticketing.domain.concert.SeatStatus;
import com.hhplus.concert_ticketing.domain.queue.QueueService;
import com.hhplus.concert_ticketing.domain.queue.TokenStatus;
import com.hhplus.concert_ticketing.domain.reservation.ReservationEntity;
import com.hhplus.concert_ticketing.domain.reservation.ReservationService;
import com.hhplus.concert_ticketing.domain.user.UserEntity;
import com.hhplus.concert_ticketing.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class ReservationFacade {

    private final QueueService queueService;
    private final ConcertService concertService;
    private final ReservationService reservationService;
    private final UserService userService;

    public void reserveSeat(String token, Long seatId, Long userId) {
        // 1. 토큰 유효성 확인
        boolean isTokenValid = queueService.checkTokenValidity(token);
        if (!isTokenValid) {
            throw new IllegalArgumentException("토큰이 만료되었습니다.");
        }

        // 2. 좌석 상태 확인
        SeatStatus seatStatus = concertService.getSeatStatus(seatId);
        if (seatStatus == SeatStatus.LOCKED) {
            throw new IllegalArgumentException("이미 선택한 좌석입니다.");
        }

        // 3. 좌석 예약 생성
        reservationService.createReservation(userId, seatId);

        // 4. 토큰 만료
        queueService.expireToken(token);
    }

    public void requestPayment(Long reservationId) {
        reservationService.requestPayment(reservationId);
    }
}
