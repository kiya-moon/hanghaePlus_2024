package io.hhplus.tdd.point.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPointDTO {
    long id;
    long point;
    long updateMillis;

    public UserPointDTO(long id, long point, long updateMillis) {
        this.id = id;
        this.point = point;
        this.updateMillis = updateMillis;
    }
}
