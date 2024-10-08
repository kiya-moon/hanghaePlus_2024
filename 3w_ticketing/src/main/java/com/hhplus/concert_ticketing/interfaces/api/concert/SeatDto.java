package com.hhplus.concert_ticketing.interfaces.api.concert;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeatDto {
    private Long id;
    private Long concertOptionId;
    private String seatNumber;
    private String status;
    private int price;
}







