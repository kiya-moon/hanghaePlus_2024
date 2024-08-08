package com.hhplus.concert_ticketing.domain.reservation;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository {
    List<Reservation> findByExpiresAtBeforeAndStatus(Timestamp now, String active);

    void save(Reservation reservation);

    Optional<Reservation> findById(Long reservationId);
}
