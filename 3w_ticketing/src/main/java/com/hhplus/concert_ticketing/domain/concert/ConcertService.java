package com.hhplus.concert_ticketing.domain.concert;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcertService {
    private final ConcertRepository concertRepository;
    private final ConcertOptionRepository concertOptionRepository;
    private final SeatRepository seatRepository;

    // 콘서트 목록 조회
    @Cacheable(value = "concerts", cacheManager = "userCacheManager")
    public List <Concert> getConcerts() {
        List<Concert> concerts = concertRepository.findAll();
        if (concerts.isEmpty()) {
            throw new IllegalArgumentException("콘서트 목록이 존재하지 않습니다.");
        }
        return concerts;
    }

    // 콘서트 정보 저장
    @CacheEvict(value = "concerts", allEntries = true)
    public void saveConcert(Concert concertEntity) {
        concertRepository.save(concertEntity);
    }

    // 콘서트 옵션 정보 저장
    @CacheEvict(value = "concertOption", allEntries = true)
    public void saveConcertOption(ConcertOption concertOption) {
        concertOptionRepository.save(concertOption);
    }

    // 예약 가능한 날짜 조회
    // 콘서트 아이디가 유효하지 않으면 IllegalArgumentException 발생
    @Cacheable(value = "concertOption", cacheManager = "userCacheManager")
    public List<ConcertOption> getAvailableDates(Long concertId) {
        return concertOptionRepository.findByConcertId(concertId)
                .orElseThrow(() -> new IllegalArgumentException("선택하신 콘서트가 존재하지 않습니다."));
    }

    // 좌석 조회
    // 콘서트 옵션 아이디가 유효하지 않으면 IllegalArgumentException 발생
    // 좌석 조회 성공 시, 선택 불가능한 좌석도 보여줘야 하므로 모든 좌석 정보를 반환
    public List<Seat> getAvailableSeats(Long concertOptionId) {
        return seatRepository.findByConcertOptionId(concertOptionId)
                .orElseThrow(() -> new IllegalArgumentException("콘서트 옵션이 유효하지 않습니다."));
    }

    // 선택한 좌석 조회
    public Seat getSeatStatus(Long seatId) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("좌석이 유효하지 않습니다."));
        if (seat.getStatus() == SeatStatus.LOCKED) {
            throw new IllegalArgumentException("이미 선택된 좌석입니다.");
        }
        return seat;
    }

    public List<ConcertOption> getAvailableConcertOptions(Long concertId) {
        return null;
    }
}
