package com.hhplus.concert_ticketing.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PaidReservationEvent {
    private String reservationId;
    private String reservation;
}
