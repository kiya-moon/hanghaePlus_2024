package com.hhplus.concert_ticketing.presentation.concert;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Seat {
    private Long id;
    private Long concertOptionId;
    private String seatNumber;
    private String status;
    private Double price;
}







