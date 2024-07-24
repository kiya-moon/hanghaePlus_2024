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
        try {
            List<ConcertEntity> concertEntities = concertService.getConcerts();
            return concertEntities.stream()
                    .map(ConcertMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("콘서트 조회 실패: {}", e.getMessage());
            throw e;
        }
    }

    // 콘서트 날짜 조회
    public List<ConcertOptionDto> getAvailableDates(Long concertId, String token) {
        try {
            // 콘서트 날짜 조회
            List<ConcertOptionEntity> concertOptionsEntity = concertService.getAvailableDates(concertId);
            return concertOptionsEntity.stream()
                    .map(ConcertMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("콘서트 날짜 조회 실패: {}", e.getMessage());
            throw e;
        }
    }

    // 콘서트 좌석 조회
    public List<SeatDto> getAvailableSeats(Long concertOptionId, String token) {
        try {
            // 콘서트 좌석 조회
            List<SeatEntity> seatEntities = concertService.getAvailableSeats(concertOptionId);
            return seatEntities.stream()
                    .map(ConcertMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("콘서트 좌석 조회 실패: {}", e.getMessage());
            throw e;
        }
    }
}
