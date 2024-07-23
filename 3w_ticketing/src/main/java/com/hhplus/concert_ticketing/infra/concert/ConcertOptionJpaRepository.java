package com.hhplus.concert_ticketing.infra.concert;

import com.hhplus.concert_ticketing.domain.concert.ConcertOptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConcertOptionJpaRepository extends JpaRepository<ConcertOptionEntity, Long> {
    Optional<List<ConcertOptionEntity>> findByConcertId(Long concertId);
}
