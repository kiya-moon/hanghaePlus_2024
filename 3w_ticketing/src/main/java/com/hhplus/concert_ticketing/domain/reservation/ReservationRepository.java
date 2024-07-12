package com.hhplus.concert_ticketing.domain.reservation;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReservationRepository {
    void save(ReservationEntity reservation);

    Optional<ReservationEntity> findById(Long reservationId);
}
