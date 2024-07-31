package com.hhplus.concert_ticketing.domain.concert;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface ConcertRepository {
    void save(ConcertEntity concertEntity);
    List<ConcertEntity> findAll();
}
