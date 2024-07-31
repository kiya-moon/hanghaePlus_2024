package com.hhplus.concert_ticketing;

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
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class SeatReservationConcurrencyTest {

    private static final Logger logger = LoggerFactory.getLogger(SeatReservationConcurrencyTest.class);

    @Autowired
    private ConcertService concertService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private Long seatId;
    private Long userId;

    @BeforeEach
    public void setUp() {

        transactionTemplate.execute(status -> {
            // 테스트용 사용자, 좌석 생성
            UserEntity user = UserEntity.createUser(1L, 100000);
            userRepository.save(user);
            userId = user.getId();

            SeatEntity seat = new SeatEntity(1L, "A1", SeatStatus.UNLOCKED, 50000);
            seatRepository.save(seat);
            seatId = seat.getId();
            return null;
        });
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
                    if (seatEntity != null && seatEntity.getStatus() == SeatStatus.UNLOCKED) {
                        try {
                            seatEntity.lockSeat();
                            seatRepository.save(seatEntity);
                            reservationService.saveReservation(userId, seatId, seatEntity.getPrice());
                        } catch (OptimisticLockingFailureException e) {
                            throw new IllegalArgumentException("이미 선택된 좌석입니다.");
                        } catch (Exception e) {
                            logger.error("좌석 예약 생성 실패: 사용자ID={}, 좌석ID={}, 에러={}", userId, seatId, e.getMessage());
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

        latch.await();

        SeatEntity seat = seatRepository.findById(seatId).orElseThrow();
        assertEquals(SeatStatus.LOCKED, seat.getStatus());
    }
}