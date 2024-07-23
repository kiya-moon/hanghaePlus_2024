package com.hhplus.concert_ticketing.presentation.reservation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReserveRequest {
    private String token;
    private Long concertOptionId;
    private Long seatId;
    private Long userId;

    public ReserveRequest(String token, long concertOptionId, long seatId, long userId) {
    }
}
