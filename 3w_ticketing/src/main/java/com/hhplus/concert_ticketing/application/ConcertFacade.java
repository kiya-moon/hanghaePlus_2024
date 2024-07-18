package com.hhplus.concert_ticketing.application;

import com.hhplus.concert_ticketing.domain.concert.*;
import com.hhplus.concert_ticketing.domain.queue.QueueService;
import com.hhplus.concert_ticketing.domain.user.UserService;
import com.hhplus.concert_ticketing.presentation.concert.Concert;
import com.hhplus.concert_ticketing.presentation.concert.ConcertOption;
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
    private final UserService userService;

    // 콘서트 조회
    public List<Concert> getConcerts() {
        List<ConcertEntity> concertEntities = concertService.getConcerts();
        return concertEntities.stream()
                .map(ConcertMapper::toDTO)
                .collect(Collectors.toList());
    }

    // 콘서트 날짜 조회
    public List<ConcertOption> getAvailableDates(Long concertId, String token) {

        // 토큰 조회 > 유효x
        // 토큰 조회 > 콘서트 날짜 조회

        // 토큰 유효성 확인
        boolean isTokenValid = queueService.checkTokenValidity(token);
        if (!isTokenValid) {
            throw new IllegalArgumentException("토큰이 만료되었습니다.");
        }

        // 콘서트 날짜 조회
        List<ConcertOptionEntity> concertOptionsEntity = concertService.getAvailableDates(concertId);
        return concertOptionsEntity.stream()
                .map(ConcertMapper::toDTO)
                .collect(Collectors.toList());
    }

    // 콘서트 좌석 조회
    public List<Seat> getAvailableSeats(Long concertOptionId, String token) {

        // 토큰 조회 > 유효x
        // 토큰 조회 > 콘서트 좌석 조회

        // 토큰 유효성 확인
        boolean isTokenValid = queueService.checkTokenValidity(token);
        if (!isTokenValid) {
            throw new IllegalArgumentException("토큰이 만료되었습니다.");
        }

        // 콘서트 좌석 조회
        List<SeatEntity> seatEntities = concertService.getAvailableSeats(concertOptionId);
        return seatEntities.stream()
                .map(ConcertMapper::toDTO)
                .collect(Collectors.toList());
    }
}
