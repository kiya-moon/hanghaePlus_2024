package com.hhplus.concert_ticketing.presentation.reservation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReserveResponse {
    private String result;
    private String message;
    private ReserveData data;
}
