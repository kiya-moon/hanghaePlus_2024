package com.hhplus.concert_ticketing.presentation.concert;

import lombok.Getter;

import java.sql.Timestamp;

@Getter
public class ConcertOptionRequest {
    private Long concertId;
    private Timestamp concertDate;
}
