package com.hhplus.concert_ticketing.domain.reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository {
    List<ReservationEntity> findByExpiresAtBeforeAndStatus(Timestamp now, String active);

    void save(ReservationEntity reservation);

    Optional<ReservationEntity> findById(Long reservationId);
}
