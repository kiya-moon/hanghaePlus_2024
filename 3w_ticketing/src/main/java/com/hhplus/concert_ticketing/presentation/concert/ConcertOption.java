package com.hhplus.concert_ticketing.presentation.concert;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConcertOption {
    private Long id;
    private Long concertId;
    private Timestamp concertDate;
    private Double price;

}
