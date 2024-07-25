package com.hhplus.concert_ticketing;

import com.hhplus.concert_ticketing.application.ReservationFacade;
import com.hhplus.concert_ticketing.domain.concert.ConcertService;
import com.hhplus.concert_ticketing.domain.concert.SeatEntity;
import com.hhplus.concert_ticketing.domain.concert.SeatRepository;
import com.hhplus.concert_ticketing.domain.concert.SeatStatus;
import com.hhplus.concert_ticketing.domain.reservation.ReservationService;
import com.hhplus.concert_ticketing.domain.user.UserEntity;
import com.hhplus.concert_ticketing.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class SeatReservationConcurrencyTest {

    private static final Logger logger = LoggerFactory.getLogger(ReservationFacade.class);

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private UserRepository userRepository;

    private Long userId;
    private Long seatId = 1L; // Example seat ID
    private String token = "exampleToken"; // Example token
    @Autowired
    private ConcertService concertService;

    @BeforeEach
    public void setUp() {
        // Insert user information
        UserEntity user = UserEntity.createUSER(1L, 100.0);
        userRepository.save(user);
        userId = user.getId();

        // Insert seat information
        SeatEntity seat = SeatEntity.createSeat(1L, 1L, "A1", SeatStatus.UNLOCKED, 50.0);
        seatRepository.save(seat);
        seatId = seat.getId();
    }

    @Test
    public void testConcurrentSeatReservation() throws InterruptedException {
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    SeatEntity seatEntity = concertService.getSeatStatus(seatId);
                    if (seatEntity != null) {
                        // 좌석 예약 생성
                        try {
                            reservationService.saveReservation(userId, seatId, seatEntity.getPrice());
                        } catch (Exception e) {
                            logger.error("좌석 예약 생성 실패: 사용자ID={}, 좌석ID={}, 에러={}", userId, seatId, e.getMessage());
                            throw e;
                        }

                        // 좌석 상태 변경
                        try {
                            seatEntity.lockSeat();
                            seatRepository.save(seatEntity);
                        } catch (Exception e) {
                            logger.error("좌석 상태 변경 실패: 좌석ID={}, 에러={}", seatId, e.getMessage());
                            throw e;
                        }
                    }
                } catch (Exception e) {
                    logger.error("에러 발생={}", e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executorService.shutdown();

        SeatEntity seat = seatRepository.findById(seatId).orElseThrow();
        assertTrue(seat.getStatus() == SeatStatus.LOCKED, "Seat should be reserved");
    }
}
