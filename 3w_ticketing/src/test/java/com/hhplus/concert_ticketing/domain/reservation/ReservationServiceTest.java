package com.hhplus.concert_ticketing.domain.reservation;

import com.hhplus.concert_ticketing.domain.concert.SeatEntity;
import com.hhplus.concert_ticketing.domain.user.UserEntity;
import com.hhplus.concert_ticketing.domain.user.UserRepository;
import com.hhplus.concert_ticketing.domain.concert.SeatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;

import static com.hhplus.concert_ticketing.domain.concert.SeatStatus.LOCKED;
import static com.hhplus.concert_ticketing.domain.concert.SeatStatus.UNLOCKED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 예약_저장_성공_테스트() {
        // given
        SeatEntity seat = mock(SeatEntity.class);
        UserEntity user = mock(UserEntity.class);

        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));
        when(seatRepository.findById(1L)).thenReturn(java.util.Optional.of(seat));
        when(seat.getStatus()).thenReturn(UNLOCKED);

        // when
        reservationService.saveReservation(1L, 1L, 100.0);

        // then
        verify(seatRepository, times(1)).save(seat);
        verify(reservationRepository, times(1)).save(any(ReservationEntity.class));
        verify(seat, times(1)).setStatus(LOCKED);
    }

    @Test
    void 예약_저장_좌석이_LOCKED인_경우_테스트() {
        // given
        SeatEntity seat = mock(SeatEntity.class);
        UserEntity user = mock(UserEntity.class);

        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));
        when(seatRepository.findById(1L)).thenReturn(java.util.Optional.of(seat));
        when(seat.getStatus()).thenReturn(LOCKED);

        // when / then
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            reservationService.saveReservation(1L, 1L, 100.0);
        });
        assertEquals("이미 선택된 좌석입니다.", thrown.getMessage());
    }

    @Test
    void 존재하지_않는_사용자_테스트() {
        // given
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        // when / then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            reservationService.saveReservation(1L, 1L, 100.0);
        });
        assertEquals("사용자가 존재하지 않습니다.", thrown.getMessage());
    }

    @Test
    void 존재하지_않는_좌석_테스트() {
        // given
        UserEntity user = mock(UserEntity.class);
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user));
        when(seatRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        // when / then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            reservationService.saveReservation(1L, 1L, 100.0);
        });
        assertEquals("좌석이 존재하지 않습니다.", thrown.getMessage());
    }

    @Test
    void 예약_조회_성공_테스트() {
        // given
        Long reservationId = 1L;
        ReservationEntity reservation = new ReservationEntity();
        reservation.setExpiresAt(new Timestamp(System.currentTimeMillis() + 10000));  // 유효한 예약

        when(reservationRepository.findById(reservationId)).thenReturn(java.util.Optional.of(reservation));

        // when
        ReservationEntity result = reservationService.getReservationInfo(reservationId);

        // then
        assertEquals(reservation, result);
    }

    @Test
    void 예약_조회_만료된_예약_테스트() {
        // given
        Long reservationId = 1L;
        ReservationEntity reservation = new ReservationEntity();
        reservation.setExpiresAt(new Timestamp(System.currentTimeMillis() - 1000));  // 이미 만료된 예약

        when(reservationRepository.findById(reservationId)).thenReturn(java.util.Optional.of(reservation));

        // when / then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reservationService.getReservationInfo(reservationId);
        });
        assertEquals("결제 시간이 만료되었습니다.", exception.getMessage());
    }
}
