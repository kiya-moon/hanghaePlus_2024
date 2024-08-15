package com.hhplus.concert_ticketing.interfaces.api.reservation;

import com.hhplus.concert_ticketing.application.ReservationFacade;
import com.hhplus.concert_ticketing.interfaces.api.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "reservation", description = "예약 관련 API")
@RequiredArgsConstructor
public class ReservationController {

    private static final Logger logger = LoggerFactory.getLogger(ReservationController.class);
    private final ReservationFacade reservationFacade;

    @PostMapping("/reserve")
    @Operation(
            summary = "좌석 예약",
            description = "토큰이 유효하다면 좌석 예약을 진행합니다.",
            responses = {
                    @ApiResponse(responseCode = "303", description = "결제 페이지로 이동합니다.",
                            headers = {
                                    @io.swagger.v3.oas.annotations.headers.Header(
                                            name = HttpHeaders.LOCATION,
                                            description = "리다이렉션할 예약 페이지의 URL",
                                            schema = @Schema(type = "string", example = "/payment-page")
                                    )
                            }
                    ),
                    @ApiResponse(responseCode = "401", description = "접근이 유효하지 않습니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(responseCode = "403", description = "이미 선택된 좌석입니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    public ResponseEntity<?> reserveSeat(@RequestBody ReserveRequest request) {
        try {
            reservationFacade.reserveSeat(request.getToken(), request.getSeatId(), request.getUserId());
            HttpHeaders headers = new HttpHeaders();
//            headers.setLocation(URI.create("/payment-page"));
            return new ResponseEntity<>(headers, HttpStatus.SEE_OTHER);
        } catch (IllegalArgumentException e) {
            String message = e.getMessage();
            logger.error("좌석 예약 중 오류 발생. 요청 데이터: 토큰={}, 좌석 ID={}, 사용자 ID={}, 오류 메시지: {}", request.getToken(), request.getSeatId(), request.getUserId(), message);
            if (message.equals("토큰이 만료되었습니다.")) {
                return new ResponseEntity<>(new ErrorResponse("401", message), HttpStatus.UNAUTHORIZED);
            } else if (message.equals("이미 선택한 좌석입니다.")) {
                return new ResponseEntity<>(new ErrorResponse("403", message), HttpStatus.FORBIDDEN);
            } else {
                return new ResponseEntity<>(new ErrorResponse("400", message), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.error("서버 오류로 인한 좌석 예약 실패. 요청 데이터: 토큰={}, 좌석 ID={}, 사용자 ID={}, 오류 메시지: {}", request.getToken(), request.getSeatId(), request.getUserId(), e.getMessage());
            return new ResponseEntity<>(new ErrorResponse("500", "서버에서 오류가 발생했습니다."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
