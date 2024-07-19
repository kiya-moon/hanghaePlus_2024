package com.hhplus.concert_ticketing.presentation.concert;

import com.hhplus.concert_ticketing.application.ConcertFacade;
import com.hhplus.concert_ticketing.presentation.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/concert")
@RequiredArgsConstructor
@Tag(name = "concert", description = "콘서트 관련 API")
public class ConcertController {

    private static final Logger logger = LoggerFactory.getLogger(ConcertController.class);
    private final ConcertFacade concertFacade;

    @GetMapping("/get-concerts")
    @Operation(
            summary = "콘서트 목록 조회",
            description = "콘서트 목록을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공적으로 콘서트 목록을 조회했습니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AvailableDatesResponse.class))
                    ),
                    @ApiResponse(responseCode = "401", description = "접근이 유효하지 않습니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    public ResponseEntity<?> getConcerts() {
        logger.info("콘서트 목록 조회 요청을 받았습니다.");
        try {
            List<Concert> concertDTOs = concertFacade.getConcerts();
            ConcertListResponse response = new ConcertListResponse(concertDTOs);
            logger.info("콘서트 목록 조회 성공: {}개 항목", concertDTOs.size());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("콘서트 목록 조회 실패: 접근이 유효하지 않습니다.", e);
            return new ResponseEntity<>(new ErrorResponse("401", "접근이 유효하지 않습니다."), HttpStatus.UNAUTHORIZED);
        }
    }

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

        logger.info("콘서트ID={}의 날짜 조회 요청을 받았습니다. 토큰={}", concertId, token);
        try {
            List<ConcertOption> concertOptionDTOs = concertFacade.getAvailableDates(concertId, token);
            AvailableDatesResponse response = new AvailableDatesResponse(concertOptionDTOs);
            logger.info("콘서트ID={}의 날짜 조회 성공: {}개 항목", concertId, concertOptionDTOs.size());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("콘서트ID={}의 날짜 조회 실패: 접근이 유효하지 않습니다.", concertId, e);
            return new ResponseEntity<>(new ErrorResponse("401", "접근이 유효하지 않습니다."), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            logger.error("콘서트ID={}의 날짜 조회 실패: 대기시간이 만료되었습니다.", concertId, e);
            return new ResponseEntity<>(new ErrorResponse("403", "대기시간이 만료되었습니다."), HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/{concertOptionId}/available-seats")
    @Operation(
            summary = "좌석 조회",
            description = "콘서트 옵션에 대한 좌석을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공적으로 콘서트 좌석을 조회했습니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AvailableSeatsResponse.class))
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

        logger.info("콘서트옵션ID={}의 좌석 조회 요청을 받았습니다. 토큰={}", concertOptionId, token);
        try {
            List<Seat> availableSeats = concertFacade.getAvailableSeats(concertOptionId, token);
            AvailableSeatsResponse response = new AvailableSeatsResponse(availableSeats);
            logger.info("콘서트옵션ID={}의 좌석 조회 성공: {}개 항목", concertOptionId, availableSeats.size());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("콘서트옵션ID={}의 좌석 조회 실패: 접근이 유효하지 않습니다.", concertOptionId, e);
            return new ResponseEntity<>(new ErrorResponse("401", "접근이 유효하지 않습니다."), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            logger.error("콘서트옵션ID={}의 좌석 조회 실패: 대기시간이 만료되었습니다.", concertOptionId, e);
            return new ResponseEntity<>(new ErrorResponse("403", "대기시간이 만료되었습니다."), HttpStatus.FORBIDDEN);
        }
    }
}
