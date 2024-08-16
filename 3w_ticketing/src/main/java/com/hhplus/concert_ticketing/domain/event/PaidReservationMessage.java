package com.hhplus.concert_ticketing.domain.event;

import com.hhplus.concert_ticketing.avro.Reservation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PaidReservationMessage {
    private Long reservationId;
    private Reservation reservation;

    // toString method
    @Override
    public String toString() {
        return "PaidReservationMessage{" +
                "reservationId='" + reservationId + '\'' +
                ", reservation=" + reservation +
                '}';
    }
}
