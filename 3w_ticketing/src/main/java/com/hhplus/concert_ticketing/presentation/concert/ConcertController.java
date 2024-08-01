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
import org.springframework.util.Assert;

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
                    @ApiResponse(responseCode = "404", description = "콘서트 목록을 찾지 못했습니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    public ResponseEntity<?> getConcerts() {
        try {
            List<ConcertDto> concertDtoDTOS = concertFacade.getConcerts();
            ConcertListResponse response = new ConcertListResponse(concertDtoDTOS);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("콘서트 목록 조회 실패: 접근이 유효하지 않습니다.", e);
            return new ResponseEntity<>(new ErrorResponse("404", "콘서트 목록을 찾지 못했습니다."), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/save-concert")
    @Operation(
            summary = "콘서트 정보 저장",
            description = "새로운 콘서트 정보를 저장합니다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "성공적으로 콘서트가 저장되었습니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConcertDto.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    public ResponseEntity<?> saveConcert(@RequestBody ConcertRequest request) {
        // 입력값 검증
        Assert.notNull(request, "콘서트 정보가 비어있습니다.");
        Assert.hasText(request.getName(), "콘서트 이름은 필수입니다.");

        try {
            // ConcertRequest를 ConcertDto로 변환
            ConcertDto concertDto = new ConcertDto(request.getName());

            // 변환된 DTO를 Facade에 전달하여 비즈니스 로직 처리
            concertFacade.saveConcert(concertDto);
            return new ResponseEntity<>(HttpStatus.CREATED); // 201 Created
        } catch (IllegalArgumentException e) {
            logger.error("콘서트 저장 실패: 잘못된 요청입니다.", e);
            return new ResponseEntity<>(new ErrorResponse("400", "잘못된 요청입니다."), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("콘서트 저장 실패: 서버 오류", e);
            return new ResponseEntity<>(new ErrorResponse("500", "서버 오류"), HttpStatus.INTERNAL_SERVER_ERROR);
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

        try {
            List<ConcertOptionDto> concertOptionDtoDTOS = concertFacade.getAvailableDates(concertId, token);
            AvailableDatesResponse response = new AvailableDatesResponse(concertOptionDtoDTOS);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.error("콘서트ID={}의 날짜 조회 실패: 접근이 유효하지 않습니다.", concertId, e);
            return new ResponseEntity<>(new ErrorResponse("401", "접근이 유효하지 않습니다."), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            logger.error("콘서트ID={}의 날짜 조회 실패: 대기시간이 만료되었습니다.", concertId, e);
            return new ResponseEntity<>(new ErrorResponse("403", "대기시간이 만료되었습니다."), HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/save-concert-option")
    @Operation(
            summary = "콘서트 정보 저장",
            description = "새로운 콘서트 정보를 저장합니다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "성공적으로 콘서트 옵션이 저장되었습니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ConcertDto.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "ㅜ잘못된 요청입니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    public ResponseEntity<?> saveConcertOption(@RequestBody ConcertOptionRequest request) {
        // 입력값 검증
        Assert.notNull(request, "콘서트 옵션 정보가 비어있습니다.");
        Assert.notNull(request.getConcertId(), "콘서트 선택은 필수입니다.");
        Assert.notNull(request.getConcertDate(), "콘서트 날짜 정보는 필수입니다.");

        try {
            // ConcertRequest를 ConcertDto로 변환
            ConcertOptionDto concertOptionDto = new ConcertOptionDto(request.getConcertId(), request.getConcertDate());

            // 변환된 DTO를 Facade에 전달하여 비즈니스 로직 처리
            concertFacade.saveConcertOption(concertOptionDto);
            return new ResponseEntity<>(HttpStatus.CREATED); // 201 Created
        } catch (IllegalArgumentException e) {
            logger.error("콘서트 옵션 저장 실패: 잘못된 요청입니다.", e);
            return new ResponseEntity<>(new ErrorResponse("400", "잘못된 요청입니다."), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("콘서트 옵션 저장 실패: 서버 오류", e);
            return new ResponseEntity<>(new ErrorResponse("500", "서버 오류"), HttpStatus.INTERNAL_SERVER_ERROR);
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

        try {
            List<SeatDto> availableSeatDtos = concertFacade.getAvailableSeats(concertOptionId, token);
            AvailableSeatsResponse response = new AvailableSeatsResponse(availableSeatDtos);
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
