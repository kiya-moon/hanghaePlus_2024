package com.hhplus.concert_ticketing.infra.concert;

import com.hhplus.concert_ticketing.domain.concert.ConcertEntity;
import com.hhplus.concert_ticketing.domain.concert.ConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ConcertRepositoryImpl implements ConcertRepository {
    private final ConcertJpaRepository concertJpaRepository;

    @Override
    public void save(ConcertEntity concertEntity) {
        concertJpaRepository.save(concertEntity);
    }

    @Override
    public List<ConcertEntity> findAll() {
        return concertJpaRepository.findAll();
    }
}
