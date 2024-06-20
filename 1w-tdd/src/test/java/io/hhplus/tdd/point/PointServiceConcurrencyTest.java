package io.hhplus.tdd.point;

import io.hhplus.tdd.point.dao.PointRepository;
import io.hhplus.tdd.point.dao.PointRepositoryImpl;
import io.hhplus.tdd.point.service.PointService;
import io.hhplus.tdd.point.vo.TransactionType;
import io.hhplus.tdd.point.vo.UserPoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PointServiceConcurrencyTest {

    private PointService pointService;
    private ExecutorService executorService;

    @BeforeEach
    void setUp() {
        PointRepository pointRepository = new PointRepositoryImpl();
        pointService = new PointService(pointRepository);
        executorService = Executors.newFixedThreadPool(10); // 최대 10개의 스레드 풀 생성
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void testConcurrentOperations() throws InterruptedException, ExecutionException {
        long id = 1L;
        long initPoint = 100L;
        long chargeAmount = 50L;
        long useAmount = 30L;

        // initPoint 설정
        pointService.chargePoint(id, initPoint);

        // 동시에 실행할 작업들을 정의
        List<Callable<Void>> tasks = new ArrayList<>();
        tasks.add(() -> {
            pointService.chargePoint(id, chargeAmount);
            return null;
        });
        tasks.add(() -> {
            pointService.usePoint(id, useAmount);
            return null;
        });

        // 작업들을 동시에 실행하고 결과를 기다림
        List<Future<Void>> futures = executorService.invokeAll(tasks);

        // 모든 작업이 완료될 때까지 기다림
        for (Future<Void> future : futures) {
            future.get();
        }

        // 최종 포인트 확인
        UserPoint userPoint = pointService.getUserPoint(id);
        assertEquals(initPoint + chargeAmount - useAmount, userPoint.point());
    }

    @AfterEach
    void tearDown() {
        if (executorService != null) {
            executorService.shutdown(); // 스레드 풀 종료
        }
    }
}