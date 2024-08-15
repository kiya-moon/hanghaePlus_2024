package com.hhplus.concert_ticketing.application;

import com.hhplus.concert_ticketing.domain.reservation.Reservation;
import com.hhplus.concert_ticketing.domain.reservation.ReservationRepository;
import com.hhplus.concert_ticketing.domain.reservation.ReservationService;
import com.hhplus.concert_ticketing.domain.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentFacade {

    private static final Logger logger = LoggerFactory.getLogger(PaymentFacade.class);

    private final UserService userService;
    private final ReservationService reservationService;
    private final ReservationRepository reservationRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    // 결제
    @Transactional
    public void payInPoint(Long userId, Long reservationId) {

        // 예약 조회
        Reservation reservation;
        try {
            reservation = reservationService.getReservationInfo(reservationId);
        } catch (Exception e) {
            logger.error("예약 ID {} 조회 실패: {}", reservationId, e.getMessage());
            throw e;
        }

        int price = reservation.getPrice();

        // 포인트 사용
        try {
            userService.usePoint(userId, price);
        } catch (Exception e) {
            logger.error("사용자 ID {} 포인트 사용 실패: {}", userId, e.getMessage());
            throw e;
        }

        // 예약 완료 처리
        try {
            reservation.completeReservation();
            reservationRepository.save(reservation);
        } catch (Exception e) {
            logger.error("예약 ID {} 완료 처리 실패: {}", reservationId, e.getMessage());
            throw e;
        }

        // 결제 완료 이벤트 outbox에 저장


        // 이벤트 발행
        applicationEventPublisher.publishEvent(new PaidEvent(this, reservation));
    }
}

