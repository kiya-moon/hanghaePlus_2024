package com.hhplus.concert_ticketing.domain.concert;

import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository {
    Optional<List<SeatEntity>> findByConcertOptionIdAndConcertDate(Long concertOptionId, Timestamp concertDate);
    Optional<SeatEntity> findById(Long seatId);
    void save(SeatEntity seat);
}
