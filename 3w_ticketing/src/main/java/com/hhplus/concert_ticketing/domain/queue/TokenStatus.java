package com.hhplus.concert_ticketing.domain.queue;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum TokenStatus {
    ACTIVE,
    WAITING,
    EXPIRED;
}
