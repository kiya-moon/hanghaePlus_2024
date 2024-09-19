package com.hhplus.concert_ticketing.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhplus.concert_ticketing.domain.event.PaidReservationEvent;
import com.hhplus.concert_ticketing.domain.reservation.Reservation;
import com.hhplus.concert_ticketing.domain.reservation.ReservationRepository;
import com.hhplus.concert_ticketing.domain.reservation.ReservationService;
import com.hhplus.concert_ticketing.domain.user.UserService;
import com.hhplus.concert_ticketing.infra.event.OutboxEvent;
import com.hhplus.concert_ticketing.infra.event.OutboxEventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PaymentFacade {

    private static final Logger logger = LoggerFactory.getLogger(PaymentFacade.class);

    private final UserService userService;
    private final ReservationService reservationService;
    private final ReservationRepository reservationRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ObjectMapper objectMapper = new ObjectMapper();

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
        try {
            OutboxEvent outboxEvent = OutboxEvent.create(
                    reservationId.toString(),
                    "payment",
                    "PaidReservationEvent",
                    "Reservation ID: " + reservationId + " 결제 완료됨"
            );
            outboxEventRepository.save(outboxEvent);
        } catch (Exception e) {
            logger.error("결제 완료 이벤트 저장 실패: {}", e.getMessage());
            throw e;
        }

        // Reservation 객체를 JSON 문자열로 변환
        String reservationJson = convertToJsonReservation(reservation);

        // 이벤트 발행
        applicationEventPublisher.publishEvent(new PaidReservationEvent(reservationId.toString(), reservationJson));
    }

    private String convertToJsonReservation(Reservation domainReservation) {
        try {
            return objectMapper.writeValueAsString(domainReservation);
        } catch (JsonProcessingException e) {
            logger.error("Reservation 객체를 JSON으로 직렬화 실패: {}", e.getMessage());
            throw new RuntimeException("JSON 직렬화 실패", e);
        }
    }
}

