package com.hhplus.concert_ticketing.domain.concert;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<SeatEntity, Long> {
    Optional<List<SeatEntity>> findByConcertOptionId(Long concertOptionId);
    Optional<SeatEntity> findById(Long seatId);
    SeatEntity save(SeatEntity seat);
}
