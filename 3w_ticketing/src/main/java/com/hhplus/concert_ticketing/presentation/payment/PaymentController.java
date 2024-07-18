package com.hhplus.concert_ticketing.presentation.payment;

import com.hhplus.concert_ticketing.application.PaymentFacade;
import com.hhplus.concert_ticketing.presentation.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
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

import lombok.RequiredArgsConstructor;

import java.net.URI;

@RestController
@RequestMapping("/api")
@Tag(name = "payment", description = "결제 관련 API")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentFacade paymentFacade;

    @PostMapping("/pay")
    @Operation(
            summary = "결제 요청",
            description = "유저 토큰과 예약 ID를 기반으로 결제 요청을 처리합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "결제가 성공적으로 처리되었습니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PayResponse.class))
                    ),
                    @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰입니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(responseCode = "500", description = "서버 오류입니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    public ResponseEntity<?> payInPoint(@RequestBody PayRequest request) {
        try {
            paymentFacade.payInPoint(request.getUserId(), request.getReservationId());
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            String message = e.getMessage();
            if (message.equals("유효하지 않은 토큰입니다.")) {
                return new ResponseEntity<>(new ErrorResponse("401", message), HttpStatus.UNAUTHORIZED);
            } else if (message.equals("잘못된 요청입니다.")) {
                return new ResponseEntity<>(new ErrorResponse("400", message), HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>(new ErrorResponse("500", "Internal server error"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }
}
