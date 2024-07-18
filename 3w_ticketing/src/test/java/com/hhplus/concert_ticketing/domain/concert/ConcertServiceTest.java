package com.hhplus.concert_ticketing.domain.concert;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.hhplus.concert_ticketing.domain.concert.SeatStatus.LOCKED;
import static com.hhplus.concert_ticketing.domain.concert.SeatStatus.UNLOCKED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ConcertServiceTest {

    @Mock
    private ConcertRepository concertRepository;

    @Mock
    private ConcertOptionRepository concertOptionRepository;

    @Mock
    private SeatRepository seatRepository;

    @InjectMocks
    private ConcertService concertService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 콘서트_목록_조회_성공() {
        List<ConcertEntity> concerts = new ArrayList<>();
        concerts.add(new ConcertEntity(1L, "2024-07-15"));
        concerts.add(new ConcertEntity(2L, "2024-07-16"));

        when(concertRepository.findAll()).thenReturn(concerts);

        List<ConcertEntity> result = concertService.getConcerts();
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void 콘서트_목록_조회_실패() {
        when(concertRepository.findAll()).thenReturn(new ArrayList<>());

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            concertService.getConcerts();
        });

        assertEquals("콘서트 목록이 존재하지 않습니다.", thrown.getMessage());
    }

    @Test
    void 예약_가능한_날짜_조회_성공() {
        Long concertId = 1L;
        List<ConcertOptionEntity> concertOptions = new ArrayList<>();
        concertOptions.add(new ConcertOptionEntity(1L, concertId, "2024-07-15", 100.0));
        concertOptions.add(new ConcertOptionEntity(2L, concertId, "2024-07-16", 150.0));

        when(concertOptionRepository.findByConcertId(concertId))
                .thenReturn(Optional.of(concertOptions));

        List<ConcertOptionEntity> result = concertService.getAvailableDates(concertId);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void 예약_가능한_날짜_조회_실패() {
        Long concertId = 2L;

        when(concertOptionRepository.findByConcertId(concertId))
                .thenReturn(Optional.empty());

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            concertService.getAvailableDates(concertId);
        });

        assertEquals("선택하신 콘서트가 존재하지 않습니다.", thrown.getMessage());
    }

    @Test
    void 좌석_조회_성공() {
        Long concertOptionId = 1L;
        List<SeatEntity> seats = new ArrayList<>();
        seats.add(new SeatEntity(1L, concertOptionId, "1", UNLOCKED));
        seats.add(new SeatEntity(2L, concertOptionId, "2", LOCKED));

        when(seatRepository.findByConcertOptionId(concertOptionId))
                .thenReturn(Optional.of(seats));

        List<SeatEntity> result = concertService.getAvailableSeats(concertOptionId);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void 좌석_조회_실패() {
        Long concertOptionId = 1L;

        when(seatRepository.findByConcertOptionId(concertOptionId))
                .thenReturn(Optional.empty());

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            concertService.getAvailableSeats(concertOptionId);
        });

        assertEquals("콘서트 옵션이 유효하지 않습니다.", thrown.getMessage());
    }

    @Test
    void 선택한_좌석_조회_성공() {
        Long seatId = 1L;
        SeatEntity seat = new SeatEntity(seatId, 1L, "1", UNLOCKED);

        when(seatRepository.findById(seatId))
                .thenReturn(Optional.of(seat));

        SeatEntity result = concertService.getSeatStatus(seatId);
        assertNotNull(result);
        assertEquals(UNLOCKED, result.getStatus());
    }

    @Test
    void 선택한_좌석_조회_실패_좌석_존재하지_않음() {
        Long seatId = 1L;

        when(seatRepository.findById(seatId))
                .thenReturn(Optional.empty());

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            concertService.getSeatStatus(seatId);
        });

        assertEquals("좌석이 유효하지 않습니다.", thrown.getMessage());
    }

    @Test
    void 선택한_좌석_조회_실패_좌석_잠금() {
        Long seatId = 1L;
        SeatEntity seat = new SeatEntity(seatId, 1L, "1", LOCKED);

        when(seatRepository.findById(seatId))
                .thenReturn(Optional.of(seat));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            concertService.getSeatStatus(seatId);
        });

        assertEquals("이미 선택된 좌석입니다.", thrown.getMessage());
    }
}
