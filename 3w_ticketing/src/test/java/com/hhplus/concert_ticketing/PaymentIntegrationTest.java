package com.hhplus.concert_ticketing;

import com.hhplus.concert_ticketing.application.PaymentFacade;
import com.hhplus.concert_ticketing.domain.reservation.Reservation;
import com.hhplus.concert_ticketing.domain.reservation.ReservationRepository;
import com.hhplus.concert_ticketing.domain.reservation.ReservationService;
import com.hhplus.concert_ticketing.domain.user.User;
import com.hhplus.concert_ticketing.domain.user.UserRepository;
import com.hhplus.concert_ticketing.domain.user.UserService;
import com.hhplus.concert_ticketing.infra.user.UserRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class PaymentIntegrationTest {
    private static final Logger logger = LoggerFactory.getLogger(UserRepositoryImpl.class);

    @Autowired
    private PaymentFacade paymentFacade;

    @Autowired
    private UserService userService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Reservation testReservation;
    private Long seatId = 1L;

    @BeforeEach
    public void setUp() {
        // 유저 생성
        testUser = User.createUser(1L, 100000);
        userRepository.save(testUser);

        // 예약 생성
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Timestamp expiresAt = new Timestamp(System.currentTimeMillis() + 5 * 60 * 1000); // 5분
        logger.info("userId : {} ", testUser.getId());
        logger.info("now : {} ", now);
        logger.info("expiresAt : {} ", expiresAt);
        testReservation = Reservation.createReservation(testUser.getId(), seatId, now, expiresAt, 50000);
        reservationRepository.save(testReservation);
        logger.info("testReservation: {}, {}, {}", testReservation.getId(), testReservation.getCreatedAt(), testReservation.getExpiresAt());
    }

    @Test
    public void 결제_포인트_테스트_성공_동시성테스트_추가() throws ExecutionException, InterruptedException {
        // Long userId = testUser.getId();
        // Long reservationId = testReservation.getId();

        // // Perform payment
        // paymentFacade.payInPoint(userId, reservationId);

        // // Verify points deduction
        // User updatedUser = userService.getUserInfo(userId);
        // assertEquals(50000, updatedUser.getBalance());

        // // Verify reservation completion
        // Reservation updatedReservation = reservationService.getReservationInfo(reservationId);
        // assertTrue(updatedReservation.getStatus().equals("COMPLETE"));
                Long userId = testUser.getId();
        Long reservationId = testReservation.getId();

        ExecutorService executor = Executors.newFixedThreadPool(5);
        Callable<Void> paymentTask = () -> {
            try {
                paymentFacade.payInPoint(userId, reservationId);
            } catch (Exception e) {
                logger.error("Payment failed", e);
            }
            return null;
        };

        // 동시에 5개의 결제 요청을 실행
        Future<Void> future1 = executor.submit(paymentTask);
        Future<Void> future2 = executor.submit(paymentTask);
        Future<Void> future3 = executor.submit(paymentTask);
        Future<Void> future4 = executor.submit(paymentTask);
        Future<Void> future5 = executor.submit(paymentTask);

        // Wait for all tasks to complete
        future1.get();
        future2.get();
        future3.get();
        future4.get();
        future5.get();

        executor.shutdown();

        // Verify points deduction
        User updatedUser = userService.getUserInfo(userId);
        assertEquals(50000, updatedUser.getBalance());

        // Verify reservation completion
        Reservation updatedReservation = reservationService.getReservationInfo(reservationId);
        assertTrue(updatedReservation.getStatus().equals("COMPLETE"));
    }

}
