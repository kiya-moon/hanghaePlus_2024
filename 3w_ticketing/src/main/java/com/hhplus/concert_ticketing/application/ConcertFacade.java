package com.hhplus.concert_ticketing.application;

import com.hhplus.concert_ticketing.domain.concert.*;
import com.hhplus.concert_ticketing.domain.queue.QueueService;
import com.hhplus.concert_ticketing.domain.user.UserService;
import com.hhplus.concert_ticketing.presentation.concert.Concert;
import com.hhplus.concert_ticketing.presentation.concert.ConcertOption;
import com.hhplus.concert_ticketing.presentation.concert.Seat;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConcertFacade {

    private static final Logger logger = LoggerFactory.getLogger(ConcertFacade.class);

    private final QueueService queueService;
    private final ConcertService concertService;

    // 콘서트 조회
    public List<Concert> getConcerts() {
        logger.info("콘서트 목록 조회 시작");
        List<ConcertEntity> concertEntities = concertService.getConcerts();
        List<Concert> concerts = concertEntities.stream()
                .map(ConcertMapper::toDTO)
                .collect(Collectors.toList());
        logger.info("콘서트 {}개 조회 완료", concerts.size());
        return concerts;
    }

    // 콘서트 날짜 조회
    public List<ConcertOption> getAvailableDates(Long concertId, String token) {
        logger.info("콘서트 ID {}의 가능한 날짜 조회 시작", concertId);

        // 콘서트 날짜 조회
        List<ConcertOptionEntity> concertOptionsEntity = concertService.getAvailableDates(concertId);
        List<ConcertOption> concertOptions = concertOptionsEntity.stream()
                .map(ConcertMapper::toDTO)
                .collect(Collectors.toList());
        logger.info("콘서트 ID {}의 가능한 날짜 {}개 조회 완료", concertId, concertOptions.size());
        return concertOptions;
    }

    // 콘서트 좌석 조회
    public List<Seat> getAvailableSeats(Long concertOptionId, String token) {
        logger.info("콘서트 옵션 ID {}의 가능한 좌석 조회 시작", concertOptionId);

        // 콘서트 좌석 조회
        List<SeatEntity> seatEntities = concertService.getAvailableSeats(concertOptionId);
        List<Seat> seats = seatEntities.stream()
                .map(ConcertMapper::toDTO)
                .collect(Collectors.toList());
        logger.info("콘서트 옵션 ID {}의 가능한 좌석 {}개 조회 완료", concertOptionId, seats.size());
        return seats;
    }
}
