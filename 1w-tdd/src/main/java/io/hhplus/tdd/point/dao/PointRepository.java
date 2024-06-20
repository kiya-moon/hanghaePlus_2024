package io.hhplus.tdd.point.dao;

import io.hhplus.tdd.point.vo.PointHistory;
import io.hhplus.tdd.point.vo.TransactionType;
import io.hhplus.tdd.point.vo.UserPoint;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointRepository {
    UserPoint insertOrUpdatePoint(long id, long amount);

    UserPoint selectById(long id);

    List<PointHistory> selectHistoriesById(long id);

    void insertHistory(long id, long amount, TransactionType type, long updateMillis);
}
