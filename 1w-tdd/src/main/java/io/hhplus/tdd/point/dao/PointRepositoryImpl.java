package io.hhplus.tdd.point.dao;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.vo.PointHistory;
import io.hhplus.tdd.point.vo.TransactionType;
import io.hhplus.tdd.point.vo.UserPoint;

import java.util.List;

public class PointRepositoryImpl implements PointRepository {
    UserPointTable userPointTable = new UserPointTable();
    PointHistoryTable pointHistoryTable = new PointHistoryTable();

    @Override
    public UserPoint insertOrUpdatePoint(long id, long amount) {
        return userPointTable.insertOrUpdate(id, amount);
    }

    @Override
    public UserPoint selectById(long id) {
        return userPointTable.selectById(id);
    }

    @Override
    public List<PointHistory> selectHistoriesById(long id) {
        return pointHistoryTable.selectAllByUserId(id);
    }

    @Override
    public void insertHistory(long id, long amount, TransactionType type, long updateMillis) {
        pointHistoryTable.insert(id, amount, type, updateMillis);
    }
}
