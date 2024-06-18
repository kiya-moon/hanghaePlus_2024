package io.hhplus.tdd.point;

import io.hhplus.tdd.point.dto.PointHistory;
import io.hhplus.tdd.point.dto.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PointServiceTest {
    private PointService pointService = new PointService();

    @BeforeEach
    void setUp() {
        // 초기화
        pointService = new PointService();
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
        pointService.setUserPoint(id, initPoint);

        // 포인트 충전
        pointService.chargePoint(id, chargeAmounts);

        // 충전 후 포인트 잔액 검증
        assertEquals(totalPoint, pointService.getUserPoint(id));
    }

    // 단위테스트 작성 2. 포인트 사용 성공 여부 테스트
    // why? 포인트 사용 기능의 가장 기본적인 테스트라고 생각되어서 작성
    // 주의점! 포인트 잔고가 부족하면 포인트를 사용할 수 없음
    @Test
    void ReturnUsePointTest() {
        // 테스트 값
        long initPoint = 100L;
        long pointsToUse = 50L;
        long expectedRemainingPoint = initPoint - pointsToUse;
        long id = 1L;

        // 사용자의 초기 포인트 값 설정
        pointService.setUserPoint(id, initPoint);

        // 포인트 사용 시도
        boolean success = pointService.usePoint(id, pointsToUse);

        // 포인트 사용 성공 여부 확인
        assertTrue(success);
        // 잔고가 제대로 감소했는지 확인
        assertEquals(expectedRemainingPoint, pointService.getUserPoint(id));
    }

    // 포인트 사용 실패 (잔고 부족) 여부 테스트
    @Test
    void returnFailUsePointInsufficientBalance() {
        // 테스트 값
        long initPoint = 20L;
        long pointsToUse = 50L;
        long id = 1L;

        // 사용자의 초기 포인트 값 설정
        pointService.setUserPoint(id, initPoint);

        // 포인트 사용 시도
        boolean success = pointService.usePoint(id, pointsToUse);

        // 포인트 사용 실패 여부 확인
        assertFalse(success);
        // 잔고가 변하지 않았는지 확인
        assertEquals(initPoint, pointService.getUserPoint(id));
    }


    // 단위테스트 작성 3. 포인트 조회 성공 여부 테스트
    // why? 포인트 조회 기능의 가장 기본적인 테스트라고 생각되어서 작성
    @Test
    void returnGetPointTest() {
        // 테스트 값
        long initPoint = 200L;
        long id = 1L;

        // 사용자의 초기 포인트 값 설정
        pointService.setUserPoint(id, initPoint);

        // 포인트 조회
        long userPoint = pointService.getUserPoint(id);

        // 조회한 포인트가 올바르게 설정한 초기 포인트와 일치하는지 검증
        assertEquals(initPoint, userPoint);
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
        pointService.setUserPoint(id, initPoint);

        // 포인트 충전 및 사용
        pointService.chargePoint(id, chargeAmount);
        pointService.usePoint(id, useAmount);

        // 포인트 내역 조회
        List<PointHistory> pointHistory = pointService.getPointHistory(id);

        // 포인트 내역의 개수 확인
        assertEquals(2, pointHistory.size());
    }

    // PointManagement innerClass로 작성
    // TODO. 분리 작업하기
    public static class PointService {
        // map으로 사용자 포인트 저장
        private final Map<Long, Long> userPoint;
        private final List<PointHistory> pointHistoryList;

        // 생성자
        public PointService() {
            this.userPoint = new HashMap<>();
            this.pointHistoryList = new ArrayList<>();
        }

        // 사용자의 포인트를 세팅하는 메서드
        public void setUserPoint(long id, long points) {
            userPoint.put(id, points);
        }

        // 사용자의 포인트를 조회하는 메서드
        public Long getUserPoint(long id) {
            return userPoint.getOrDefault(id, 0L);
        }

        // 사용자의 포인트를 충전하는 메서드
        public void chargePoint(long id, long points) {
            long currentPoints = getUserPoint(id);
            userPoint.put(id, currentPoints + points);
            // 포인트 내역 기록 메서드 추가
            recordPointHistory(id, points, TransactionType.CHARGE);
        }

        // 사용자의 포인트 사용 가능 여부를 체크하는 메서드
        public boolean usePoint(long id, long pointsToUse) {
            long currentPoints = getUserPoint(id);
            if (currentPoints >= pointsToUse) {
                userPoint.put(id, currentPoints - pointsToUse);
                // 포인트 내역 기록 메서드 추가
                recordPointHistory(id, pointsToUse, TransactionType.USE);
                return true;
            } else {
                return false;
            }
        }

        // 포인트 내역 기록하는 메서드
        private void recordPointHistory(long id, long amount, TransactionType type) {
            pointHistoryList.add(new PointHistory(
                    System.currentTimeMillis(), id, amount, type, System.currentTimeMillis()
            ));
        }

        // 포인트 조회해오는 메서드
        public List<PointHistory> getPointHistory(long id) {
            List<PointHistory> userHistory = new ArrayList<>();
            for (PointHistory history : pointHistoryList) {
                if (history.userId() == id) {
                    userHistory.add(history);
                }
            }
            return userHistory;
        }
    }
}

