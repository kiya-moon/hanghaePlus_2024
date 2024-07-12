package com.hhplus.concert_ticketing.domain.concert;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ConcertServiceTest {

    private ConcertService concertService;
    private ConcertOptionRepository concertOptionRepository;
    private SeatRepository seatRepository;

    @BeforeEach
    void setUp() {
        concertOptionRepository = Mockito.mock(ConcertOptionRepository.class);
        seatRepository = Mockito.mock(SeatRepository.class);

        concertService = new ConcertService(concertOptionRepository, seatRepository);
    }

    @Test
    void 콘서트_날짜_조회_성공() {
        Long concertId = 1L;
        List<ConcertOptionEntity> concertOptions = new ArrayList<>();
        concertOptions.add(new ConcertOptionEntity(1L, concertId, "2024-07-15", 100.0));
        concertOptions.add(new ConcertOptionEntity(2L, concertId, "2024-07-16", 150.0));

        Mockito.when(concertOptionRepository.findByConcertId(concertId))
                .thenReturn(Optional.of(concertOptions));

        List<ConcertOptionEntity> result = concertService.getAvailableDates(concertId);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void 콘서트_날짜_조회_실패() {
        Long concertId = 2L;

        Mockito.when(concertOptionRepository.findByConcertId(concertId))
                .thenReturn(Optional.empty());

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            concertService.getAvailableDates(concertId);
        });

        assertEquals("콘서트가 존재하지 않습니다.", thrown.getMessage());
    }

    @Test
    void 좌석_조회_성공() {
        Long concertOptionId = 1L;
        Timestamp concertDate = Timestamp.valueOf("2024-07-15 00:00:00");
        List<SeatEntity> seats = new ArrayList<>();
        seats.add(new SeatEntity(1L, concertOptionId, "1", "UNLOCKED"));
        seats.add(new SeatEntity(2L, concertOptionId, "2", "LOCKED"));

        Mockito.when(seatRepository.findByConcertOptionIdAndConcertDate(concertOptionId, concertDate))
                .thenReturn(Optional.of(seats));

        List<SeatEntity> result = concertService.getAvailableSeats(concertOptionId, concertDate);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void 좌석_조회_실패() {
        Long concertOptionId = 1L;
        Timestamp concertDate = Timestamp.valueOf("2024-07-15 00:00:00");

        Mockito.when(seatRepository.findByConcertOptionIdAndConcertDate(concertOptionId, concertDate))
                .thenReturn(Optional.empty());

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            concertService.getAvailableSeats(concertOptionId, concertDate);
        });

        assertEquals("콘서트 옵션이 유효하지 않습니다.", thrown.getMessage());
    }
}
