package com.hhplus.concert_ticketing.domain.concert;

import java.util.List;

public interface ConcertRepository {
    void save(Concert concertEntity);
    List<Concert> findAll();
}
