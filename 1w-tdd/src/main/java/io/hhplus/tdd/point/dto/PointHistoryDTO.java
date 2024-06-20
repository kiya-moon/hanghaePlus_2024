package io.hhplus.tdd.point.dto;

import io.hhplus.tdd.point.vo.TransactionType;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PointHistoryDTO {
    long id;
    long userId;
    long amount;
    TransactionType type;
    long updateMillis;

    public PointHistoryDTO(long id, long userId, long amount, TransactionType type, long updateMillis) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.type = type;
        this.updateMillis = updateMillis;
    }

}
