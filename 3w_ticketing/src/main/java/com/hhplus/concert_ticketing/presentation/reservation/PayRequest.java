package com.hhplus.concert_ticketing.presentation.reservation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayRequest {
    private String token;
    private Long reservationId;
    private Double amount;
}
