package com.hhplus.concert_ticketing.infra.concert;

import com.hhplus.concert_ticketing.domain.concert.Concert;
import com.hhplus.concert_ticketing.domain.concert.ConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ConcertRepositoryImpl implements ConcertRepository {
    private final ConcertJpaRepository concertJpaRepository;

    @Override
    public void save(Concert concertEntity) {
        concertJpaRepository.save(concertEntity);
    }

    @Override
    public List<Concert> findAll() {
        return concertJpaRepository.findAll();
    }
}
