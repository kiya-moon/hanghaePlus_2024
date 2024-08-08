package com.hhplus.concert_ticketing;

import com.hhplus.concert_ticketing.application.ReservationFacade;
import com.hhplus.concert_ticketing.domain.concert.SeatRepository;
import com.hhplus.concert_ticketing.domain.queue.QueueRepository;
import com.hhplus.concert_ticketing.domain.queue.Token;
import com.hhplus.concert_ticketing.domain.queue.TokenStatus;
import com.hhplus.concert_ticketing.domain.user.User;
import com.hhplus.concert_ticketing.domain.user.UserRepository;
import com.hhplus.concert_ticketing.presentation.ErrorResponse;
import com.hhplus.concert_ticketing.presentation.reservation.ReserveRequest;
import com.hhplus.concert_ticketing.presentation.reservation.ReservationController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
class ReservationIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(SeatReservationConcurrencyTest.class);

    @Autowired
    private ReservationController reservationController;

    @Autowired
    private ReservationFacade reservationFacade;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private QueueRepository queueRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private Long seatId;
    private List<Long> userIds;

    @BeforeEach
    public void setUp() {
        userIds = new ArrayList<>();
        transactionTemplate.execute(status -> {
            // 유저 10명 생성
            for (int i = 0; i < 10; i++) {
                User user = User.createUser((long) (i + 1), 100000);
                userRepository.save(user);
                userIds.add(user.getId());
            }

            // 토큰 10개 생성(모두 활성화 상태로 가정)
            for (int i = 0; i < 10; i++) {
                Timestamp now = new Timestamp(System.currentTimeMillis());
                Timestamp expiresAt = new Timestamp(System.currentTimeMillis() + 5 * 60 * 1000); // 5분
                Token token = Token.createToken("token-" + (i + 1), userIds.get(i), TokenStatus.ACTIVE, now, expiresAt);
                queueRepository.save(token);
            }

//            // 좌석 생성
//            SeatEntity seat = SeatEntity.createSeat(1L, 1L, "A1", SeatStatus.UNLOCKED, 50.0);
//            seatRepository.save(seat);
//            seatId = seat.getId();
            return null;
        });
    }

    @Test
    void reserveSeat_성공() throws InterruptedException {
        // given
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            Long userId = userIds.get(i);
            String token = "token-" + (i + 1);
            executorService.submit(() -> {
                try {
                    // when
                    ReserveRequest request = new ReserveRequest(token, 1L, 1L, userId);
                    ResponseEntity<?> result = reservationController.reserveSeat(request);

                    // then
                    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.SEE_OTHER);
                    assertThat(Objects.requireNonNull(result.getHeaders().getLocation()).toString()).isEqualTo("/payment-page");
                } catch (Exception e) {
                    // 예외 발생 시 로깅
                    logger.error("예외 발생: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
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
