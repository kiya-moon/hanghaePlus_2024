package com.hhplus.concert_ticketing.domain.concert;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface ConcertOptionRepository {
    Optional<List<ConcertOptionEntity>> findByConcertId(Long concertId);

    void save(ConcertOptionEntity concertOptionEntity);
}
