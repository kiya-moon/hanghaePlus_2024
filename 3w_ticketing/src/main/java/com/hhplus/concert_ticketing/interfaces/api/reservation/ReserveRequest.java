package com.hhplus.concert_ticketing.interfaces.api.reservation;

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
}
