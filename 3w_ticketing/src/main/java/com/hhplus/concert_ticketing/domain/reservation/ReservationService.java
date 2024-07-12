package com.hhplus.concert_ticketing.domain.reservation;

import com.hhplus.concert_ticketing.domain.concert.SeatEntity;
import com.hhplus.concert_ticketing.domain.concert.SeatRepository;
import com.hhplus.concert_ticketing.domain.user.UserEntity;
import com.hhplus.concert_ticketing.domain.user.UserRepository;
import jakarta.transaction.Transactional;
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

    // 예약 생성
    // 선택한 좌석의 status 확인하여 UNLOCKED 상태인 경우에만 예약 가능
    // LOCKED 상태라면 403, 이미 선택된 좌석입니다. 반환
    // 좌석 테이블에서 선택된 좌석의 status를 LOCKED로 변경하고 예약 정보 저장
    @Transactional
    public void createReservation(Long userId, Long seatId) {
        // 사용자와 좌석 존재 확인
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        SeatEntity seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("좌석이 존재하지 않습니다."));

        // 좌석 상태가 UNLOCKED인지 확인
        if (!"UNLOCKED".equals(seat.getStatus())) {
            throw new IllegalStateException("이미 선택된 좌석입니다.");
        }

        // 좌석 상태를 LOCKED로 변경
        seat.setStatus("LOCKED");
        seatRepository.save(seat);

        // 예약 정보 저장
        ReservationEntity reservation = new ReservationEntity();
        reservation.setUserId(userId);
        reservation.setSeatId(seatId);
        reservationRepository.save(reservation);
    }


    // 결제 요청 메서드
    @Transactional
    public void requestPayment(Long reservationId) {
        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NoSuchElementException("예약이 존재하지 않습니다."));

        if (reservation.getExpiredAt().before(new Timestamp(System.currentTimeMillis()))) {
            throw new IllegalStateException("결제 시간이 만료되었습니다.");
        }

        UserEntity user = userRepository.findById(reservation.getUserId())
                .orElseThrow(() -> new NoSuchElementException("유저가 존재하지 않습니다."));

        double price = reservation.getPrice();  // 예약 가격을 가져오는 메서드
        double userBalance = user.getBalance();

        if (userBalance < price) {
            throw new IllegalStateException("잔액이 부족합니다.");
        }

        user.setBalance(userBalance - price);
        userRepository.save(user);

        reservation.setStatus("CONFIRMED");
        reservationRepository.save(reservation);
    }
}
