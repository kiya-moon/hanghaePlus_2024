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
import org.springframework.stereotype.Service;

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
    public void reserveSeat(String token, Long seatId, Long userId) {
        logger.info("좌석 예약 시작: 토큰={}, 좌석ID={}, 사용자ID={}", token, seatId, userId);

        // 토큰 유효성 확인
        boolean isTokenValid = queueService.checkToken(token).getStatus() == TokenStatus.ACTIVE;
        if (!isTokenValid) {
            logger.warn("토큰이 만료되었습니다: 토큰={}", token);
            throw new IllegalArgumentException("토큰이 만료되었습니다.");
        }
        logger.info("토큰 유효성 확인 완료: 토큰={}", token);

        // 사용자 조회
        UserEntity user;
        try {
            user = userService.getUserInfo(userId);
            logger.info("사용자 조회 성공: 사용자ID={}", userId);
        } catch (Exception e) {
            logger.error("사용자 조회 실패: 사용자ID={}, 에러={}", userId, e.getMessage());
            throw e;
        }

        // 좌석 상태 확인
        SeatEntity seatEntity;
        try {
            seatEntity = concertService.getSeatStatus(seatId);
            logger.info("좌석 상태 확인 성공: 좌석ID={}", seatId);
        } catch (Exception e) {
            logger.error("좌석 상태 확인 실패: 좌석ID={}, 에러={}", seatId, e.getMessage());
            throw e;
        }

        // 좌석 예약 생성
        try {
            reservationService.saveReservation(userId, seatId, seatEntity.getPrice());
            logger.info("좌석 예약 생성 성공: 사용자ID={}, 좌석ID={}", userId, seatId);
        } catch (Exception e) {
            logger.error("좌석 예약 생성 실패: 사용자ID={}, 좌석ID={}, 에러={}", userId, seatId, e.getMessage());
            throw e;
        }

        // 좌석 상태 변경
        try {
            seatEntity.lockSeat();
            seatRepository.save(seatEntity);
            logger.info("좌석 상태 변경 성공: 좌석ID={}, 상태={}", seatId, SeatStatus.LOCKED);
        } catch (Exception e) {
            logger.error("좌석 상태 변경 실패: 좌석ID={}, 에러={}", seatId, e.getMessage());
            throw e;
        }

        // 토큰 만료
        try {
            queueService.expireToken(token);
            logger.info("토큰 만료 처리 성공: 토큰={}", token);
        } catch (Exception e) {
            logger.error("토큰 만료 처리 실패: 토큰={}, 에러={}", token, e.getMessage());
            throw e;
        }
    }

    // 예약 상태 관리
    public void manageReservationStatus() {
        logger.info("예약 상태 관리 시작");
        Timestamp now = new Timestamp(System.currentTimeMillis());

        try {
            List<ReservationEntity> expiredReservations = reservationRepository.findByExpiresAtBeforeAndStatus(now, "ACTIVE");
            for (ReservationEntity reservation : expiredReservations) {
                reservation.expireReservation();
                reservationRepository.save(reservation);
                logger.info("예약 만료 처리 성공: 예약ID={}", reservation.getId());

                SeatEntity seat = seatRepository.findById(reservation.getSeatId()).orElse(null);
                if (seat != null) {
                    seat.unlockSeat();
                    seatRepository.save(seat);
                    logger.info("좌석 상태 변경 성공: 좌석ID={}, 상태={}", seat.getId(), SeatStatus.UNLOCKED);
                }
            }
        } catch (Exception e) {
            logger.error("예약 상태 관리 실패: 에러={}", e.getMessage());
            throw e;
        }
    }
}
