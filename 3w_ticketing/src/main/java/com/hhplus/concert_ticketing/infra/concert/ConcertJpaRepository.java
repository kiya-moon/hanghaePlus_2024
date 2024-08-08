package com.hhplus.concert_ticketing.infra.concert;

import com.hhplus.concert_ticketing.domain.concert.Concert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertJpaRepository extends JpaRepository<Concert, Long>{
}
