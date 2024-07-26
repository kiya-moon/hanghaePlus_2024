package com.hhplus.concert_ticketing;

import com.hhplus.concert_ticketing.application.UserFacade;
import com.hhplus.concert_ticketing.domain.user.UserEntity;
import com.hhplus.concert_ticketing.domain.user.UserRepository;
import com.hhplus.concert_ticketing.presentation.ErrorResponse;
import com.hhplus.concert_ticketing.presentation.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.NoSuchElementException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserIntegrationTest {

    @Autowired
    private UserController userController;

    @Autowired
    private UserFacade userFacade;

    @Autowired
    private UserRepository userRepository;

    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        // 유저 생성
        testUser = UserEntity.createUser(1L, 100.0);
        userRepository.save(testUser);
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
    void chargeBalance_비관적락테스트_추가() throws InterruptedException {
        // given
        int numberOfRequests = 5; // 보내는 요청의 수
        double chargeAmount = 10.0; // 각 요청에서 충전할 포인트 양
        double finalBalance = testUser.getBalance() + (chargeAmount * numberOfRequests); // 예상 최종 잔액

        // CountDownLatch를 사용하여 모든 요청이 완료될 때까지 대기
        CountDownLatch latch = new CountDownLatch(numberOfRequests);

        // ExecutorService를 사용하여 요청을 병렬로 실행
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfRequests);

        // when
        for (int i = 0; i < numberOfRequests; i++) {
            executorService.submit(() -> {
                try {
                    ChargeRequest request = new ChargeRequest(testUser.getId(), chargeAmount);
                    userController.chargeBalance(request);
                } finally {
                    latch.countDown(); // 요청 완료 시 카운트다운
                }
            });
        }

        // 모든 요청이 완료될 때까지 10초 동안 대기
        latch.await(10, TimeUnit.SECONDS);

        // then
        // 최종 잔액이 예상한 값과 일치하는지 검증
        UserEntity updatedUser = userRepository.findById(testUser.getId())
                .orElseThrow(() -> new IllegalStateException("사용자 정보가 없습니다."));
        assertThat(updatedUser.getBalance()).isEqualTo(finalBalance);

        // ExecutorService 종료
        executorService.shutdown();
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
