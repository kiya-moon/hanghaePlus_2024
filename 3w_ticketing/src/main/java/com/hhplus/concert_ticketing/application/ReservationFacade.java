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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationFacade {

    private static final Logger logger = LoggerFactory.getLogger(ReservationFacade.class);

    private final QueueService queueService;
    private final ConcertService concertService;
    private final ReservationService reservationService;
    private final UserService userService;

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;

    // 좌석 예약
    @Transactional
    public void reserveSeat(String token, Long seatId, Long userId) {

        // 사용자 조회
        UserEntity user;
        try {
            user = userService.getUserInfo(userId);
        } catch (Exception e) {
            logger.error("사용자 조회 실패: 사용자ID={}, 에러={}", userId, e.getMessage());
            throw e;
        }

        // 좌석 상태 확인
        SeatEntity seatEntity;
        try {
            seatEntity = concertService.getSeatStatus(seatId);
        } catch (Exception e) {
            logger.error("좌석 상태 확인 실패: 좌석ID={}, 에러={}", seatId, e.getMessage());
            throw e;
        }

        // 좌석 상태 변경 및 예약
        try {
            seatEntity.lockSeat();
            // 변경된 좌석 상태 저장 시 버전 증가
            seatRepository.save(seatEntity);
            reservationService.saveReservation(userId, seatId, seatEntity.getPrice());
        // 버전이 다른 사람들은 에러로 처리
        } catch (OptimisticLockingFailureException e) {
            logger.error("좌석 예약 실패: 좌석ID={}, 에러={}", seatId, e.getMessage());
            throw new IllegalArgumentException("이미 선택된 좌석입니다.");
        } catch (Exception e) {
            logger.error("좌석 예약 생성 실패: 사용자ID={}, 좌석ID={}, 에러={}", userId, seatId, e.getMessage());
            throw e;
        }

        // 토큰 만료
        try {
            queueService.expireToken(token);
        } catch (Exception e) {
            logger.error("토큰 만료 처리 실패: 토큰={}, 에러={}", token, e.getMessage());
            throw e;
        }
    }

    // 예약 상태 관리
    public void manageReservationStatus() {
        Timestamp now = new Timestamp(System.currentTimeMillis());

        try {
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
        } catch (Exception e) {
            logger.error("예약 상태 관리 실패: 에러={}", e.getMessage());
            throw e;
        }
    }
}
