package com.hhplus.concert_ticketing.infra.concert;

import com.hhplus.concert_ticketing.domain.concert.SeatEntity;
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
    public Optional<List<SeatEntity>> findByConcertOptionId(Long concertOptionId) {
        return seatJpaRepository.findByConcertOptionId(concertOptionId);
    }

    @Override
    public Optional<SeatEntity> findById(Long seatId) {
        return seatJpaRepository.findById(seatId);
    }

    @Override
    public void save(SeatEntity seat) {
        seatJpaRepository.save(seat);
    }

    @Override
    public Optional<SeatEntity> findByIdWithLock(Long seatId) {
        return seatJpaRepository.findByIdWithLock(seatId);
    }

}
