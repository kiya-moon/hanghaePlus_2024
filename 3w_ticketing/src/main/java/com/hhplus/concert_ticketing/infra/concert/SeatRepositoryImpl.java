package com.hhplus.concert_ticketing.infra.concert;

import com.hhplus.concert_ticketing.domain.concert.Seat;
import com.hhplus.concert_ticketing.domain.concert.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SeatRepositoryImpl implements SeatRepository {
    private final SeatJpaRepository seatJpaRepository;

    @Override
    public Optional<List<Seat>> findByConcertOptionId(Long concertOptionId) {
        return seatJpaRepository.findByConcertOptionId(concertOptionId);
    }

    @Override
    public Optional<Seat> findById(Long seatId) {
        return seatJpaRepository.findById(seatId);
    }

    @Override
    public void save(Seat seat) {
        seatJpaRepository.save(seat);
    }

}
