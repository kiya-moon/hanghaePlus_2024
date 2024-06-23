package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.dao.PointRepository;
import io.hhplus.tdd.point.vo.PointHistory;
import io.hhplus.tdd.point.vo.TransactionType;
import io.hhplus.tdd.point.vo.UserPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class PointService {

    // 동시성 이슈를 해결하기 위해 ReentrantLock 사용
    private final ReentrantLock lock = new ReentrantLock();

    // map으로 사용자 포인트 저장
//    private final Map<Long, Long> userPoint;
    // list로 포인트 내역 저장
//    private final List<PointHistory> pointHistoryList;

    // 레파지토리 추가
    private final PointRepository pointRepository;

    // 생성자
    public PointService(PointRepository pointRepository) {
//        this.userPoint = new HashMap<>();
//        this.pointHistoryList = new ArrayList<>();
        this.pointRepository = pointRepository;
    }

    // 포인트 조회 메서드
    public UserPoint getUserPoint(long id) {
//        long point = userPoint.getOrDefault(id, 0L);
//        return new UserPoint(id, point, System.currentTimeMillis());
        return pointRepository.selectById(id);
    }

    // 포인트 충전 메서드
    public void chargePoint(long id, long points) {
//        UserPoint currentPoints = getUserPoint(id);
//        userPoint.put(id, currentPoints.point() + points);
//        // 포인트 내역 기록 메서드 추가
//        recordPointHistory(id, points, TransactionType.CHARGE);
        lock.lock();
        try {
            UserPoint currentPoints = getUserPoint(id);
            pointRepository.insertOrUpdatePoint(id, currentPoints.point() + points);
            pointRepository.insertHistory(id, points, TransactionType.CHARGE, System.currentTimeMillis());
        } finally {
            lock.unlock();
        }
    }

    // 포인트 사용 후 성공 여부 반환 메서드
    public boolean usePoint(long id, long pointsToUse) {
        lock.lock();
        try {
            UserPoint currentPoints = getUserPoint(id);
            if (currentPoints.point() >= pointsToUse) {
//            userPoint.put(id, currentPoints.point() - pointsToUse);
//            // 포인트 내역 기록 메서드 추가
//            recordPointHistory(id, pointsToUse, TransactionType.USE);
                pointRepository.insertOrUpdatePoint(id, currentPoints.point() - pointsToUse);
                pointRepository.insertHistory(id, pointsToUse, TransactionType.USE, System.currentTimeMillis());
                return true;
            } else {
                return false;
            }
        } finally {
            lock.unlock();
        }
    }

    // 포인트 내역 기록 메서드
//    private void recordPointHistory(long id, long amount, TransactionType type) {
//        pointHistoryList.add(new PointHistory(
//                System.currentTimeMillis(), id, amount, type, System.currentTimeMillis()
//        ));
//    }

    // 포인트 내역 조회 메서드
    public List<PointHistory> getPointHistory(long id) {
//        List<PointHistory> userHistory = new ArrayList<>();
//        for (PointHistory history : pointHistoryList) {
//            if (history.userId() == id) {
//                userHistory.add(history);
//            }
//        }
//        return userHistory;
        return pointRepository.selectHistoriesById(id);
    }
}
