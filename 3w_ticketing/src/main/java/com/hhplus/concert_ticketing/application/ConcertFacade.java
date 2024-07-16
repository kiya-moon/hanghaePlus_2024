package com.hhplus.concert_ticketing.application;

import com.hhplus.concert_ticketing.domain.concert.SeatEntity;
import com.hhplus.concert_ticketing.domain.queue.QueueService;
import com.hhplus.concert_ticketing.domain.concert.ConcertOptionEntity;
import com.hhplus.concert_ticketing.domain.concert.ConcertService;
import com.hhplus.concert_ticketing.presentation.concert.Seat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConcertFacade {

    private final QueueService queueService;
    private final ConcertService concertService;

    public List<ConcertOptionEntity> getAvailableDates(Long concertId, String token) {
        if (!queueService.checkTokenValidity(token)) {
            throw new IllegalArgumentException("유효하지 않은 접근입니다.");
        }
        return concertService.getAvailableDates(concertId);
    }

    public List<Seat> getAvailableSeats(Long concertOptionId, String token) {
        if (!queueService.checkTokenValidity(token)) {
            throw new IllegalArgumentException("유효하지 않은 접근입니다.");
        }

        List<SeatEntity> seatEntities = concertService.getAvailableSeats(concertOptionId);

        // SeatEntity를 Seat로 변환
        return seatEntities.stream()
                .map(entity -> new Seat(
                        entity.getId(),
                        entity.getConcertOptionId(),
                        entity.getSeatNumber(),
                        entity.getStatus().toString()  // 여기에 .toString() 호출
                ))
                .collect(Collectors.toList());
    }
}
