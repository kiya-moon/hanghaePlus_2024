package com.hhplus.concert_ticketing.presentation.reservation;

import com.hhplus.concert_ticketing.presentation.ErrorResponse;
import com.hhplus.concert_ticketing.presentation.queue.TokenData;
import com.hhplus.concert_ticketing.presentation.queue.TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api")
@Tag(name = "reservation", description = "예약 관련 API")
public class ReservationController {

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
                                            schema = @Schema(type = "string", example = "/reservation-page")
                                    )
                            }
                    ),
                    @ApiResponse(responseCode = "401", description = "접근이 유효하지 않습니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(responseCode = "403", description = "이미 선택된 좌석 입니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    public ResponseEntity<?> reserveSeat(@RequestBody ReserveRequest request) {
        if (request.getToken() == null || request.getToken().isEmpty()) {
            return new ResponseEntity<>(new ErrorResponse("401", "접근이 유효하지 않습니다."), HttpStatus.UNAUTHORIZED);
        }

        // 좌석 상태 체크 로직 예시
        boolean isSeatLocked = checkSeatStatus(request.getSeatId());
        if (!isSeatLocked) {
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create("/payment-page"));
            return new ResponseEntity<>(headers, HttpStatus.SEE_OTHER);
        } else {
            return new ResponseEntity<>(new ErrorResponse("403", "좌석이 잠금 상태입니다."), HttpStatus.FORBIDDEN);
        }
    }

    private boolean checkSeatStatus(Long seatId) {
        // 좌석 상태 체크 로직 구현
        // 잠금 상태라면 true, 그렇지 않다면 false 반환
        return false; // 예시로 잠금 상태가 아닌 경우를 반환
    }

    @PostMapping("/pay")
    @Operation(
            summary = "결제 처리",
            description = "결제 요청을 처리합니다. 유효한 토큰이 필요하며, 결제 요청 본문에 예약 ID와 결제 금액이 필요합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "결제 요청 본문",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PayRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "결제가 성공적으로 처리되었습니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PayResponse.class))
                    ),
                    @ApiResponse(responseCode = "402", description = "잔액이 부족합니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(responseCode = "408", description = "예약 시간이 만료되었습니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    public ResponseEntity<?> pay(@RequestBody PayRequest request) {
        // 예약 만료 시간 확인 로직 예시
        boolean isExpired = checkReservationExpiry(request.getReservationId());
        if (isExpired) {
            return new ResponseEntity<>(new ErrorResponse("408", "예약 시간이 만료되었습니다."), HttpStatus.REQUEST_TIMEOUT);
        }

        // 유저의 잔액 확인 로직 예시
        boolean isBalanceSufficient = checkUserBalance(request.getUserId(), request.getAmount());
        if (!isBalanceSufficient) {
            return new ResponseEntity<>(new ErrorResponse("402", "잔액이 부족합니다."), HttpStatus.PAYMENT_REQUIRED);
        }

        // 결제 처리 로직
        PayResponse response = new PayResponse("200", "Success", new PayData(456L));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private boolean checkReservationExpiry(Long reservationId) {
        // 예약 만료 시간 확인 로직 구현
        // 만료되었으면 true, 그렇지 않다면 false 반환
        return false; // 예시로 만료되지 않은 경우를 반환
    }

    private boolean checkUserBalance(Long userId, Double amount) {
        // 유저의 잔액 확인 로직 구현
        // 잔액이 충분하면 true, 부족하면 false 반환
        return true; // 예시로 잔액이 충분한 경우를 반환
    }

}
