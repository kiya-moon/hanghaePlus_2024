package com.hhplus.concert_ticketing;

import com.hhplus.concert_ticketing.application.PaymentFacade;
import com.hhplus.concert_ticketing.domain.reservation.ReservationEntity;
import com.hhplus.concert_ticketing.domain.reservation.ReservationRepository;
import com.hhplus.concert_ticketing.domain.reservation.ReservationService;
import com.hhplus.concert_ticketing.domain.user.UserEntity;
import com.hhplus.concert_ticketing.domain.user.UserRepository;
import com.hhplus.concert_ticketing.domain.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class PaymentIntegrationTest {

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

    private UserEntity testUser;
    private ReservationEntity testReservation;
    private Long seatId = 1L;

    @BeforeEach
    public void setUp() {
        // 유저 생성
        testUser = UserEntity.createUser(1L, 100.0);
        userRepository.save(testUser);

        // 예약 생성
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Timestamp expiresAt = new Timestamp(System.currentTimeMillis() + 5 * 60 * 1000); // 5분
        testReservation = ReservationEntity.createReservation(testUser.getId(), seatId, now, expiresAt, 50.0);
        reservationRepository.save(testReservation);
    }

    @Test
    public void 결제_포인트_테스트_성공() {
        Long userId = testUser.getId();
        Long reservationId = testReservation.getId();

        // Perform payment
        paymentFacade.payInPoint(userId, reservationId);

        // Verify points deduction
        UserEntity updatedUser = userService.getUserInfo(userId);
        assertEquals(50.0, updatedUser.getBalance());

        // Verify reservation completion
        ReservationEntity updatedReservation = reservationService.getReservationInfo(reservationId);
        assertTrue(updatedReservation.getStatus().equals("COMPLETE"));
    }

}
