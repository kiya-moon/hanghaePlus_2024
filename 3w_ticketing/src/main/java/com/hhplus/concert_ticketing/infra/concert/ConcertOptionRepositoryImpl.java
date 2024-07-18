package com.hhplus.concert_ticketing.infra.concert;

import com.hhplus.concert_ticketing.domain.concert.ConcertOptionEntity;
import com.hhplus.concert_ticketing.domain.concert.ConcertOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ConcertOptionRepositoryImpl implements ConcertOptionRepository {
    private final ConcertOptionJpaRepository concertOptionJpaRepository;

    @Override
    public Optional<List<ConcertOptionEntity>> findByConcertId(Long concertId) {
        return concertOptionJpaRepository.findByConcertId(concertId);
    }
}
