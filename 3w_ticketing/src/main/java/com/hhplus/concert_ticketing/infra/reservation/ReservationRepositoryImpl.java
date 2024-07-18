package com.hhplus.concert_ticketing.infra.reservation;

import com.hhplus.concert_ticketing.domain.reservation.ReservationEntity;
import com.hhplus.concert_ticketing.domain.reservation.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepository {
    private final ReservationJpaRepository reservationJpaRepository;

    @Override
    public List<ReservationEntity> findByExpiresAtBeforeAndStatus(Timestamp now, String active) {
        return reservationJpaRepository.findByExpiresAtBeforeAndStatus(now, active);
    }

    @Override
    public void save(ReservationEntity reservation) {
        reservationJpaRepository.save(reservation);
    }

    @Override
    public Optional<ReservationEntity> findById(Long reservationId) {
        return reservationJpaRepository.findById(reservationId);
    }
}
