package com.hhplus.concert_ticketing.domain.reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {
    ReservationEntity save(ReservationEntity reservation);

    Optional<ReservationEntity> findById(Long reservationId);

    List<ReservationEntity> findByExpiresAtBeforeAndStatus(Timestamp now, String active);
}
