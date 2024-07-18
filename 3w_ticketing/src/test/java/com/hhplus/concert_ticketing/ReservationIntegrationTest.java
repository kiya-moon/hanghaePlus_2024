package com.hhplus.concert_ticketing;

import com.hhplus.concert_ticketing.application.ReservationFacade;
import com.hhplus.concert_ticketing.presentation.ErrorResponse;
import com.hhplus.concert_ticketing.presentation.reservation.ReserveRequest;
import com.hhplus.concert_ticketing.presentation.reservation.ReservationController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
class ReservationIntegrationTest {

    @Autowired
    private ReservationController reservationController;

    @Autowired
    private ReservationFacade reservationFacade;

    @Test
    void reserveSeat_성공() {
        // given
        ReserveRequest request = new ReserveRequest("valid-token", 1L, 1L, 123L);
        doNothing().when(reservationFacade).reserveSeat(request.getToken(), request.getSeatId(), request.getUserId());

        // when
        ResponseEntity<?> result = reservationController.reserveSeat(request);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.SEE_OTHER);
        assertThat(Objects.requireNonNull(result.getHeaders().getLocation()).toString()).isEqualTo("/payment-page");
    }

    @Test
    void reserveSeat_토큰만료_실패() {
        // given
        ReserveRequest request = new ReserveRequest("expired-token", 1L, 1L, 123L);
        doThrow(new IllegalArgumentException("토큰이 만료되었습니다.")).when(reservationFacade)
                .reserveSeat(request.getToken(), request.getSeatId(), request.getUserId());

        // when
        ResponseEntity<?> result = reservationController.reserveSeat(request);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(result.getBody()).isEqualTo(new ErrorResponse("401", "토큰이 만료되었습니다."));
    }

    @Test
    void reserveSeat_좌석이미선택_실패() {
        // given
        ReserveRequest request = new ReserveRequest("valid-token", 1L, 1L, 123L);
        doThrow(new IllegalArgumentException("이미 선택한 좌석입니다.")).when(reservationFacade)
                .reserveSeat(request.getToken(), request.getSeatId(), request.getUserId());

        // when
        ResponseEntity<?> result = reservationController.reserveSeat(request);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(result.getBody()).isEqualTo(new ErrorResponse("403", "이미 선택한 좌석입니다."));
    }

    @Test
    void reserveSeat_기타오류_실패() {
        // given
        ReserveRequest request = new ReserveRequest("valid-token", 1L, 1L, 123L);
        doThrow(new IllegalArgumentException("기타 오류")).when(reservationFacade)
                .reserveSeat(request.getToken(), request.getSeatId(), request.getUserId());

        // when
        ResponseEntity<?> result = reservationController.reserveSeat(request);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).isEqualTo(new ErrorResponse("400", "기타 오류"));
    }
}
