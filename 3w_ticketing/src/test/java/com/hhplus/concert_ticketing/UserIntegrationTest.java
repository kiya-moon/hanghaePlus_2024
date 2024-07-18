package com.hhplus.concert_ticketing;

import com.hhplus.concert_ticketing.application.UserFacade;
import com.hhplus.concert_ticketing.presentation.ErrorResponse;
import com.hhplus.concert_ticketing.presentation.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserIntegrationTest {

    @Autowired
    private UserController userController;

    @Autowired
    private UserFacade userFacade;

    @BeforeEach
    void setUp() {
        // Initialize necessary configurations or objects if needed
    }

    @Test
    void getBalance_성공() {
        // given
        Long userId = 1L;
        Double balance = 100.0;
        BalanceResponse response = new BalanceResponse(balance);

        when(userFacade.getBalance(userId)).thenReturn(balance);

        // when
        ResponseEntity<?> result = userController.getBalance(userId);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void getBalance_실패() {
        // given
        Long userId = 1L;

        when(userFacade.getBalance(userId)).thenThrow(new NoSuchElementException("유저가 존재하지 않습니다."));

        // when
        ResponseEntity<?> result = userController.getBalance(userId);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(result.getBody()).isEqualTo(new ErrorResponse("401", "접근이 유효하지 않습니다."));
    }

    @Test
    void chargeBalance_성공() {
        // given
        ChargeRequest request = new ChargeRequest(1L, 50.0);
        Double newBalance = 150.0;
        ChargeResponse response = new ChargeResponse(newBalance);

        when(userFacade.chargePoint(request.getUserId(), request.getAmount())).thenReturn(newBalance);

        // when
        ResponseEntity<?> result = userController.chargeBalance(request);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void chargeBalance_유효하지않은값() {
        // given
        ChargeRequest request = new ChargeRequest(1L, -50.0);

        // when
        ResponseEntity<?> result = userController.chargeBalance(request);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(result.getBody()).isEqualTo(new ErrorResponse("400", "값이 유효하지 않습니다. 관리자에게 문의해주세요."));
    }

    @Test
    void chargeBalance_실패() {
        // given
        ChargeRequest request = new ChargeRequest(1L, 50.0);

        when(userFacade.chargePoint(request.getUserId(), request.getAmount()))
                .thenThrow(new NoSuchElementException("유저가 존재하지 않습니다."));

        // when
        ResponseEntity<?> result = userController.chargeBalance(request);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(result.getBody()).isEqualTo(new ErrorResponse("401", "접근이 유효하지 않습니다."));
    }

    @Test
    void chargeBalance_서버오류() {
        // given
        ChargeRequest request = new ChargeRequest(1L, 50.0);

        when(userFacade.chargePoint(request.getUserId(), request.getAmount()))
                .thenThrow(new RuntimeException("서버 오류"));

        // when
        ResponseEntity<?> result = userController.chargeBalance(request);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(result.getBody()).isEqualTo(new ErrorResponse("500", "서버 오류로 인해 잔액 충전에 실패했습니다."));
    }
}
