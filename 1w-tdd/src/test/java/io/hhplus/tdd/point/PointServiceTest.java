package io.hhplus.tdd.point;

import io.hhplus.tdd.point.dao.PointRepository;
import io.hhplus.tdd.point.dao.PointRepositoryImpl;
import io.hhplus.tdd.point.vo.PointHistory;
import io.hhplus.tdd.point.service.PointService;
import io.hhplus.tdd.point.vo.UserPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class PointServiceTest {
    private PointService pointService;

    @BeforeEach
    void setUp() {
        // 초기화
        PointRepository pointRepository = new PointRepositoryImpl();
        pointService = new PointService(pointRepository);
    }

    // 단위테스트 작성 1.포인트 충전 성공 여부 테스트
    // why? 포인트 충전 기능의 가장 기본적인 테스트라고 생각되어서 작성
    @Test
    void returnChargePointTest() {
        // 테스트 값
        long initPoint = 0L;
        long chargeAmounts = 50L;
        long totalPoint = initPoint + chargeAmounts;
        long id = 1L;

        // 사용자의 초기 포인트 값 설정
        pointService.getUserPoint(id);

        // 포인트 충전
        pointService.chargePoint(id, chargeAmounts);

        // 충전 후 포인트 잔액 검증
        assertEquals(totalPoint, pointService.getUserPoint(id).point());
    }

    // 단위테스트 작성 2. 포인트 사용 성공 여부 테스트
    // why? 포인트 사용 기능의 가장 기본적인 테스트라고 생각되어서 작성
    // 주의점! 포인트 잔고가 부족하면 포인트를 사용할 수 없음
    @Test
    void ReturnUsePointTest() {
        // 테스트 값
        long id = 1L;
        long initPoint = 100L;
        long pointsToUse = 50L;
        long expectedRemainingPoint = initPoint - pointsToUse;

        // initPoint 설정
        pointService.chargePoint(id, initPoint);

        // 포인트 사용 시도
        boolean success = pointService.usePoint(id, pointsToUse);

        // 포인트 사용 성공 여부 확인
        assertTrue(success);
        // 잔고가 제대로 감소했는지 확인
        assertEquals(expectedRemainingPoint, pointService.getUserPoint(id).point());
    }

    // 포인트 사용 실패 (잔고 부족) 여부 테스트
    @Test
    void returnFailUsePointInsufficientBalance() {
        // 테스트 값
        long initPoint = 20L;
        long pointsToUse = 50L;
        long id = 1L;

        // initPoint 설정
        pointService.chargePoint(id, initPoint);

        // 포인트 사용 시도
        boolean success = pointService.usePoint(id, pointsToUse);

        // 포인트 사용 실패 여부 확인
        assertFalse(success);
        // 잔고가 변하지 않았는지 확인
        assertEquals(initPoint, pointService.getUserPoint(id).point());
    }


    // 단위테스트 작성 3. 포인트 조회 성공 여부 테스트
    // why? 포인트 조회 기능의 가장 기본적인 테스트라고 생각되어서 작성
    @Test
    void returnGetPointTest() {
        // 테스트 값
        long initPoint = 200L;
        long id = 1L;

        // initPoint 설정
        pointService.chargePoint(id, initPoint);

        // 포인트 조회
        UserPoint userPoint = pointService.getUserPoint(id);

        // 조회한 포인트가 올바르게 설정한 초기 포인트와 일치하는지 검증
        assertEquals(initPoint, userPoint.point());
        assertEquals(id, userPoint.id());

    }

    // 단위테스트 작성 4. 포인트 내역 조회 성공 여부 테스트
    // why? 포인트 내역 조회 기능의 가장 기본적인 테스트라고 생각되어서 작성
    @Test
    void returnGetPointHistoryTest() {
        // 테스트 값
        long initPoint = 200L;
        long chargeAmount = 50L;
        long useAmount = 30L;
        long id = 1L;

        // 사용자의 초기 포인트 값 설정
        pointService.getUserPoint(id);

        // 포인트 충전 및 사용
        pointService.chargePoint(id, chargeAmount);
        pointService.usePoint(id, useAmount);

        // 포인트 내역 조회
        List<PointHistory> pointHistory = pointService.getPointHistory(id);

        // 포인트 내역의 개수 확인
        assertEquals(2, pointHistory.size());
    }
}

