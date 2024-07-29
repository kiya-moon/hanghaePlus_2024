package com.hhplus.concert_ticketing;

import com.hhplus.concert_ticketing.application.ConcertFacade;
import com.hhplus.concert_ticketing.presentation.ErrorResponse;
import com.hhplus.concert_ticketing.presentation.concert.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
class ConcertIntegrationTest {

    @Autowired
    private ConcertController concertController;

    @Autowired
    private ConcertFacade concertFacade;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getConcerts_성공() throws Exception {
        // given
        List<ConcertDto> concertDtos = List.of(
                new ConcertDto(1L, "아이유 콘서트"),
                new ConcertDto(2L, "비투비 콘서트")
        );
        ConcertListResponse response = new ConcertListResponse(concertDtos);

        // Mock the ConcertFacade to return predefined values
        when(concertFacade.getConcerts()).thenReturn(concertDtos);

        // when
        ResponseEntity<?> result = concertController.getConcerts();

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void getConcerts_실패() throws Exception {
        // given
        when(concertFacade.getConcerts()).thenThrow(new IllegalArgumentException("콘서트 목록이 존재하지 않습니다."));

        // when
        ResponseEntity<?> result = concertController.getConcerts();

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(result.getBody()).isEqualTo(new ErrorResponse("401", "접근이 유효하지 않습니다."));
    }

    @Test
    void getAvailableDates_성공() throws Exception {
        // given
        Long concertId = 1L;
        String token = "valid-token";
        List<ConcertOptionDto> options = List.of(
                new ConcertOptionDto(1L, concertId, Timestamp.valueOf("2024-07-15 00:00:00")),
                new ConcertOptionDto(2L, concertId, Timestamp.valueOf("2024-07-16 00:00:00"))
        );
        AvailableDatesResponse response = new AvailableDatesResponse(options);

        // Mock the ConcertFacade to return predefined values
        when(concertFacade.getAvailableDates(concertId, token)).thenReturn(options);

        // when
        ResponseEntity<?> result = concertController.getAvailableDates(concertId, token);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void getAvailableDates_실패() throws Exception {
        // given
        Long concertId = 2L;
        String token = "invalid-token";

        // Mock the ConcertFacade to throw an exception
        when(concertFacade.getAvailableDates(concertId, token))
                .thenThrow(new IllegalArgumentException("선택하신 콘서트가 존재하지 않습니다."));

        // when
        ResponseEntity<?> result = concertController.getAvailableDates(concertId, token);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(result.getBody()).isEqualTo(new ErrorResponse("401", "접근이 유효하지 않습니다."));
    }

    @Test
    void getAvailableSeats_성공() throws Exception {
        // given
        Long concertOptionId = 1L;
        String token = "valid-token";
        List<SeatDto> seatDtos = List.of(
                new SeatDto(1L, concertOptionId, "1", "UNLOCKED", 150000D),
                new SeatDto(2L, concertOptionId, "2", "LOCKED", 120000D)
        );
        AvailableSeatsResponse response = new AvailableSeatsResponse(seatDtos);

        // Mock the ConcertFacade to return predefined values
        when(concertFacade.getAvailableSeats(concertOptionId, token)).thenReturn(seatDtos);

        // when
        ResponseEntity<?> result = concertController.getAvailableSeats(concertOptionId, token);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(response);
    }

    @Test
    void getAvailableSeats_실패() throws Exception {
        // given
        Long concertOptionId = 1L;
        String token = "invalid-token";

        // Mock the ConcertFacade to throw an exception
        when(concertFacade.getAvailableSeats(concertOptionId, token))
                .thenThrow(new Exception("대기시간이 만료되었습니다."));

        // when
        ResponseEntity<?> result = concertController.getAvailableSeats(concertOptionId, token);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(result.getBody()).isEqualTo(new ErrorResponse("403", "대기시간이 만료되었습니다."));
    }
}
