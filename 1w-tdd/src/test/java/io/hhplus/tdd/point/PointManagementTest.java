package io.hhplus.tdd.point;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PointManagementTest {
    private PointManagement pointManagement = new PointManagement();

    @BeforeEach
    void setUp() {
        // 초기화
        pointManagement = new PointManagement();
    }

    // 테스트 작성 - 포인트 충전 성공 여부 테스트
    // why? 포인트 충전 기능의 가장 기본적인 테스트라고 생각되어서 작성
    @Test
    void isChargePointSuccess() {
        // 테스트 값
        int initPoint = 0;
        int chargeAmounts = 50;
        int totalPoint = initPoint + chargeAmounts;
        String userId = "testUser";

        // 사용자의 초기 포인트 값 설정
        pointManagement.setUserPoint(userId, initPoint);

        // 포인트 충전
        pointManagement.chargePoint(userId, chargeAmounts);

        // 충전 후 포인트 잔액 검증
        assertEquals(totalPoint, pointManagement.getUserPoint(userId));
    }

    // PointManagement innerClass로 작성
    // TODO. 분리 작업하기
    public static class PointManagement {
        // map으로 사용자 포인트 저장
        private final Map<String, Integer> userPoints;

        // 생성자
        public PointManagement() {
            this.userPoints = new HashMap<>();
        }

        // 사용자의 포인트를 세팅하는 메서드
        public void setUserPoint(String userId, int points) {
            userPoints.put(userId, points);
        }

        // 사용자의 포인트를 조회하는 메서드
        public int getUserPoint(String userId) {
            return userPoints.getOrDefault(userId, 0);
        }

        // 사용자의 포인트를 충전하는 메서드
        public void chargePoint(String userId, int points) {
            int currentPoints = getUserPoint(userId);
            userPoints.put(userId, currentPoints + points);
        }
    }
}

