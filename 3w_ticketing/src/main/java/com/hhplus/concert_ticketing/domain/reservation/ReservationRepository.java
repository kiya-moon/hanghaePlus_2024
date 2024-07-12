package com.hhplus.concert_ticketing.domain.reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {
    ReservationEntity save(ReservationEntity reservation);

    Optional<ReservationEntity> findById(Long reservationId);
}
