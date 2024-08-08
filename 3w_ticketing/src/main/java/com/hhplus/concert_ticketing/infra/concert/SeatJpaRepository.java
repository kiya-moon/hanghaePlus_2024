package com.hhplus.concert_ticketing.infra.concert;

import com.hhplus.concert_ticketing.domain.concert.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SeatJpaRepository extends JpaRepository<Seat, Long> {
    Optional<List<Seat>> findByConcertOptionId(Long concertOptionId);
}
