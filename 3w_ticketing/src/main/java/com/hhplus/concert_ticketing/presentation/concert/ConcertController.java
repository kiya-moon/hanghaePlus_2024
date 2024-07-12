package com.hhplus.concert_ticketing.presentation.concert;

import com.hhplus.concert_ticketing.presentation.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "concert", description = "콘서트 관련 API")
public class ConcertController {
    @GetMapping("/{concertId}/available-dates")
    @Operation(
            summary = "콘서트 날짜 조회",
            description = "콘서트 날짜를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공적으로 콘서트 날짜를 조회했습니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AvailableDatesResponse.class))
                    ),
                    @ApiResponse(responseCode = "401", description = "접근이 유효하지 않습니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(responseCode = "403", description = "대기시간이 만료되었습니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    public ResponseEntity<?> getAvailableDates(
            @PathVariable Long concertId,
            @RequestParam String token) {

        if (token == null || token.isEmpty()) {
            return new ResponseEntity<>(new ErrorResponse("401", "접근이 유효하지 않습니다."), HttpStatus.UNAUTHORIZED);
        }

        AvailableDatesResponse response = new AvailableDatesResponse(List.of(
                new ConcertOption(1L, "2024-07-04"),
                new ConcertOption(2L, "2024-07-05")
        ));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{concertOptionId}/available-seats")
    @Operation(
            summary = "좌석 조회",
            description = "콘서트 옵션에 대한 좌석을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공적으로 콘서트 날짜를 조회했습니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AvailableDatesResponse.class))
                    ),
                    @ApiResponse(responseCode = "401", description = "접근이 유효하지 않습니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(responseCode = "403", description = "대기시간이 만료되었습니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    public ResponseEntity<?> getAvailableSeats(
            @PathVariable Long concertOptionId,
            @RequestParam String token) {

        if (token == null || token.isEmpty()) {
            return new ResponseEntity<>(new ErrorResponse("401", "접근이 유효하지 않습니다."), HttpStatus.UNAUTHORIZED);
        }

        AvailableSeatsResponse response = new AvailableSeatsResponse(List.of(
                new Seat(1L, "A1", "열림"),
                new Seat(2L, "A2", "열림")
        ));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
