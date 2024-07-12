package com.hhplus.concert_ticketing.domain.concert;

import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConcertOptionRepository {
    Optional<List<ConcertOptionEntity>> findByConcertId(Long concertId);
}
