package com.hhplus.concert_ticketing.presentation.user;

import com.hhplus.concert_ticketing.application.UserFacade;
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

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/user")
@Tag(name = "user", description = "사용자 관련 API")
@RequiredArgsConstructor
public class UserController {
    private final UserFacade userFacade;

    @GetMapping("/balance")
    @Operation(
            summary = "잔액 조회",
            description = "유저의 포인트 잔액을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공적으로 잔액을 조회했습니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BalanceResponse.class))
                    ),
                    @ApiResponse(responseCode = "401", description = "접근이 유효하지 않습니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    public ResponseEntity<?> getBalance(@RequestParam Long userId) {
        try {
            Double balance = userFacade.getBalance(userId);
            BalanceResponse response = new BalanceResponse(balance);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(new ErrorResponse("401", "접근이 유효하지 않습니다."), HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/balance/charge")
    @Operation(
            summary = "잔액 충전",
            description = "유저의 포인트 잔액을 충전합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공적으로 잔액이 충전되었습니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChargeResponse.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "값이 유효하지 않습니다. 관리자에게 문의해주세요.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(responseCode = "401", description = "접근이 유효하지 않습니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(responseCode = "500", description = "서버 오류로 인해 잔액 충전에 실패했습니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    public ResponseEntity<?> chargeBalance(@RequestBody ChargeRequest request) {

        if (request.getAmount() == null || request.getAmount() <= 0) {
            return new ResponseEntity<>(new ErrorResponse("400", "값이 유효하지 않습니다. 관리자에게 문의해주세요."), HttpStatus.BAD_REQUEST);
        }

        try {
            Double newBalance = userFacade.chargePoint(request.getUserId(), request.getAmount());
            ChargeResponse response = new ChargeResponse(newBalance);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(new ErrorResponse("401", "접근이 유효하지 않습니다."), HttpStatus.NOT_FOUND);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(new ErrorResponse("500", "서버 오류로 인해 잔액 충전에 실패했습니다."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
