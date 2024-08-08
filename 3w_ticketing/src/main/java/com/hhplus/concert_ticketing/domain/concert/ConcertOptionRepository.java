package com.hhplus.concert_ticketing.domain.concert;

import java.util.List;
import java.util.Optional;

public interface ConcertOptionRepository {
    Optional<List<ConcertOption>> findByConcertId(Long concertId);

    void save(ConcertOption concertOption);
}
