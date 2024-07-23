package com.hhplus.concert_ticketing.infra.reservation;

import com.hhplus.concert_ticketing.domain.reservation.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.List;

public interface ReservationJpaRepository extends JpaRepository<ReservationEntity, Long> {
    List<ReservationEntity> findByExpiresAtBeforeAndStatus(Timestamp now, String active);
}
