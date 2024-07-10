package com.hhplus.concert_ticketing.presentation.concert;

import com.hhplus.concert_ticketing.presentation.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ConcertController {
    @GetMapping("/{concertId}/available-dates")
    public ResponseEntity<?> getAvailableDates(
            @PathVariable Long concertId,
            @RequestParam String token) {

        if (token == null || token.isEmpty()) {
            return new ResponseEntity<>(new ErrorResponse("401", "대기시간이 초과되었습니다."), HttpStatus.UNAUTHORIZED);
        }

        AvailableDatesResponse response = new AvailableDatesResponse(List.of(
                new ConcertOption(1L, "2024-07-04"),
                new ConcertOption(2L, "2024-07-05")
        ));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{concertOptionId}/available-seats")
    public ResponseEntity<?> getAvailableSeats(
            @PathVariable Long concertOptionId,
            @RequestParam String token) {

        if (token == null || token.isEmpty()) {
            return new ResponseEntity<>(new ErrorResponse("401", "대기시간이 초과되었습니다."), HttpStatus.UNAUTHORIZED);
        }

        AvailableSeatsResponse response = new AvailableSeatsResponse(List.of(
                new Seat(1L, "A1", "열림"),
                new Seat(2L, "A2", "열림")
        ));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
