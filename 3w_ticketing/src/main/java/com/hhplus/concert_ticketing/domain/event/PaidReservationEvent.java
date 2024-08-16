package com.hhplus.concert_ticketing.domain.event;

import com.hhplus.concert_ticketing.avro.Reservation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PaidReservationEvent {
    private Long reservationId;
    private Reservation reservation;
}
