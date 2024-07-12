package com.hhplus.concert_ticketing.domain.reservation;

import com.hhplus.concert_ticketing.domain.concert.SeatEntity;
import com.hhplus.concert_ticketing.domain.user.UserEntity;
import com.hhplus.concert_ticketing.domain.user.UserRepository;
import com.hhplus.concert_ticketing.domain.concert.SeatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    private ReservationService reservationService;
    private SeatRepository seatRepository;
    private ReservationRepository reservationRepository;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        seatRepository = Mockito.mock(SeatRepository.class);
        reservationRepository = Mockito.mock(ReservationRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        reservationService = new ReservationService(seatRepository, reservationRepository, userRepository);
    }

    @Test
    void 예약_생성_성공_테스트() {
        // given
        UserEntity user = new UserEntity();
        user.setId(1L);
        SeatEntity seat = new SeatEntity();
        seat.setId(1L);
        seat.setStatus("UNLOCKED");

        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));
        when(seatRepository.findById(1L)).thenReturn(java.util.Optional.of(seat));

        // when
        reservationService.createReservation(1L, 1L);

        // then
        verify(seatRepository, times(1)).save(seat);
        verify(reservationRepository, times(1)).save(any(ReservationEntity.class));
        assertEquals("LOCKED", seat.getStatus());
    }

    @Test
    void 예약_생성_좌석이_LOCKED인_경우_테스트() {
        // given
        UserEntity user = new UserEntity();
        user.setId(1L);
        SeatEntity seat = new SeatEntity();
        seat.setId(1L);
        seat.setStatus("LOCKED");

        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));
        when(seatRepository.findById(1L)).thenReturn(java.util.Optional.of(seat));

        // when / then
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            reservationService.createReservation(1L, 1L);
        });
        assertEquals("이미 선택된 좌석입니다.", thrown.getMessage());
    }

    @Test
    void 존재하지_않는_사용자_테스트() {
        // given
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        // when / then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            reservationService.createReservation(1L, 1L);
        });
        assertEquals("사용자가 존재하지 않습니다.", thrown.getMessage());
    }

    @Test
    void 존재하지_않는_좌석_테스트() {
        // given
        UserEntity user = new UserEntity();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));
        when(seatRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        // when / then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            reservationService.createReservation(1L, 1L);
        });
        assertEquals("좌석이 존재하지 않습니다.", thrown.getMessage());
    }


    @Test
    void 결제_시간_만료_테스트() {
        // given
        Long reservationId = 1L;
        ReservationEntity reservation = new ReservationEntity();
        reservation.setExpiredAt(new Timestamp(System.currentTimeMillis() - 1000));  // 이미 만료된 예약

        when(reservationRepository.findById(reservationId)).thenReturn(java.util.Optional.of(reservation));

        // when / then
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            reservationService.requestPayment(reservationId);
        });

        assertEquals("결제 시간이 만료되었습니다.", exception.getMessage());
    }

    @Test
    void 잔액_부족_테스트() {
        // given
        Long reservationId = 1L;
        Long userId = 1L;
        double price = 100.0;

        ReservationEntity reservation = new ReservationEntity();
        reservation.setUserId(userId);
        reservation.setExpiredAt(new Timestamp(System.currentTimeMillis() + 10000));  // 유효한 예약
        reservation.setPrice(price);  // 예약 가격 설정

        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setBalance(50.0);  // 잔액 부족

        when(reservationRepository.findById(reservationId)).thenReturn(java.util.Optional.of(reservation));
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));

        // when / then
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            reservationService.requestPayment(reservationId);
        });

        assertEquals("잔액이 부족합니다.", exception.getMessage());
    }

    @Test
    void 결제_성공_테스트() {
        // given
        Long reservationId = 1L;
        Long userId = 1L;
        double price = 100.0;

        ReservationEntity reservation = new ReservationEntity();
        reservation.setUserId(userId);
        reservation.setExpiredAt(new Timestamp(System.currentTimeMillis() + 10000));  // 유효한 예약
        reservation.setPrice(price);  // 예약 가격 설정

        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setBalance(150.0);  // 충분한 잔액

        when(reservationRepository.findById(reservationId)).thenReturn(java.util.Optional.of(reservation));
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));

        // when
        reservationService.requestPayment(reservationId);

        // then
        assertEquals(50.0, user.getBalance());
        assertEquals("CONFIRMED", reservation.getStatus());
        verify(userRepository, times(1)).save(user);
        verify(reservationRepository, times(1)).save(reservation);
    }
}
