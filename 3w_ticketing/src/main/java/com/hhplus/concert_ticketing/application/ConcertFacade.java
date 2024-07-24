package com.hhplus.concert_ticketing.application;

import com.hhplus.concert_ticketing.domain.concert.*;
import com.hhplus.concert_ticketing.domain.queue.QueueService;
import com.hhplus.concert_ticketing.presentation.concert.ConcertDto;
import com.hhplus.concert_ticketing.presentation.concert.ConcertOptionDto;
import com.hhplus.concert_ticketing.presentation.concert.SeatDto;
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
    public List<ConcertDto> getConcerts() {
        logger.info("콘서트 목록 조회 시작");
        List<ConcertEntity> concertEntities = concertService.getConcerts();
        List<ConcertDto> concertDtos = concertEntities.stream()
                .map(ConcertMapper::toDTO)
                .collect(Collectors.toList());
        logger.info("콘서트 {}개 조회 완료", concertDtos.size());
        return concertDtos;
    }

    // 콘서트 날짜 조회
    public List<ConcertOptionDto> getAvailableDates(Long concertId, String token) {
        logger.info("콘서트 ID {}의 가능한 날짜 조회 시작", concertId);

        // 콘서트 날짜 조회
        List<ConcertOptionEntity> concertOptionsEntity = concertService.getAvailableDates(concertId);
        List<ConcertOptionDto> concertOptionDtos = concertOptionsEntity.stream()
                .map(ConcertMapper::toDTO)
                .collect(Collectors.toList());
        logger.info("콘서트 ID {}의 가능한 날짜 {}개 조회 완료", concertId, concertOptionDtos.size());
        return concertOptionDtos;
    }

    // 콘서트 좌석 조회
    public List<SeatDto> getAvailableSeats(Long concertOptionId, String token) {
        logger.info("콘서트 옵션 ID {}의 가능한 좌석 조회 시작", concertOptionId);

        // 콘서트 좌석 조회
        List<SeatEntity> seatEntities = concertService.getAvailableSeats(concertOptionId);
        List<SeatDto> seatDtos = seatEntities.stream()
                .map(ConcertMapper::toDTO)
                .collect(Collectors.toList());
        logger.info("콘서트 옵션 ID {}의 가능한 좌석 {}개 조회 완료", concertOptionId, seatDtos.size());
        return seatDtos;
    }
}
