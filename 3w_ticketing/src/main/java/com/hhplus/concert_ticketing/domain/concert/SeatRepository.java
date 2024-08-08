package com.hhplus.concert_ticketing.domain.concert;

import java.util.List;
import java.util.Optional;

public interface SeatRepository {
    Optional<List<Seat>> findByConcertOptionId(Long concertOptionId);

    Optional<Seat> findById(Long seatId);

    void save(Seat seat);
}
