package com.hhplus.concert_ticketing.domain.concert;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcertService {
    private final ConcertOptionRepository concertOptionRepository;
    private final SeatRepository seatRepository;

    // 예약 가능한 날짜 조회
    // 콘서트 아이디가 유효하지 않으면 IllegalArgumentException 발생
    public List<ConcertOptionEntity> getAvailableDates(Long concertId) {
        return concertOptionRepository.findByConcertId(concertId)
                .orElseThrow(() -> new IllegalArgumentException("콘서트가 존재하지 않습니다."));
    }

    // 좌석 조회
    // 콘서트 옵션 아이디가 유효하지 않으면 IllegalArgumentException 발생
    // 좌석 조회 성공 시, 선택 불가능한 좌석도 보여줘야 하므로 모든 좌석 정보를 반환
    public List<SeatEntity> getAvailableSeats(Long concertOptionId) {
        return seatRepository.findByConcertOptionId(concertOptionId)
                .orElseThrow(() -> new IllegalArgumentException("콘서트 옵션이 유효하지 않습니다."));
    }

    // 선택한 좌석 상태 조회
    public SeatStatus getSeatStatus(Long seatId) {
        return seatRepository.findById(seatId).orElseThrow().getStatus();
    }
}
