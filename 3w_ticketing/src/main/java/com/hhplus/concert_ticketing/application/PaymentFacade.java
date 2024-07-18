package com.hhplus.concert_ticketing.application;

import com.hhplus.concert_ticketing.domain.reservation.ReservationEntity;
import com.hhplus.concert_ticketing.domain.reservation.ReservationRepository;
import com.hhplus.concert_ticketing.domain.reservation.ReservationService;
import com.hhplus.concert_ticketing.domain.user.UserEntity;
import com.hhplus.concert_ticketing.domain.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentFacade {
    UserService userService;
    ReservationService reservationService;

    ReservationRepository reservationRepository;

    // 결제
    @Transactional
    public void payInPoint(Long userId, Long reservationId) {
        // 사용자 조회
        // 사용자 조회 > 유효x
        // 사용자 조회 > 예약 조회
        // 사용자 조회 > 예약 조회 > 유효x
        // 사용자 조회 > 예약 조회 > 잔액조회
        // 사용자 조회 > 예약 조회 > 잔액조회 > 포인트 부족
        // 사용자 조회 > 예약 조회 > 잔액조회 > 결제 > 예약 완료

        // 사용자 조회
        UserEntity user = userService.getUserInfo(userId);

        // 예약 조회
        ReservationEntity reservation = reservationService.getReservationInfo(reservationId);
        double price = reservation.getPrice();

        // 포인트 사용
        userService.usePoint(userId, price);

        // 예약 완료 처리
        reservation.completeReservation();
        reservationRepository.save(reservation);
    }
}
