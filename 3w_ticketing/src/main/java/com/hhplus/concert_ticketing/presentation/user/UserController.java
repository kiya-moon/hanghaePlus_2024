package com.hhplus.concert_ticketing.presentation.user;

import com.hhplus.concert_ticketing.presentation.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Tag(name = "user", description = "사용자 관련 API")
public class UserController {
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
        // 유저 존재 여부 확인 로직 예시
        boolean userExists = checkUserExists(userId);
        if (!userExists) {
            return new ResponseEntity<>(new ErrorResponse("401", "접근이 유효하지 않습니다."), HttpStatus.NOT_FOUND);
        }

        // 잔액 조회 로직
        BalanceResponse response = new BalanceResponse(5000.00);
        return new ResponseEntity<>(response, HttpStatus.OK);
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

        // 유저 존재 여부 확인 로직 예시
        boolean userExists = checkUserExists(request.getUserId());
        if (!userExists) {
            return new ResponseEntity<>(new ErrorResponse("401", "접근이 유효하지 않습니다."), HttpStatus.NOT_FOUND);
        }

        try {
            // 현재 잔액 조회
            double currentBalance = getCurrentBalance(request.getUserId());
            // 잔액 충전 처리
            double updatedBalance = chargeUserBalance(request.getUserId(), request.getAmount());

            // 충전 후 잔액 검증
            if (Math.abs(currentBalance + request.getAmount() - updatedBalance) > 0.01) { // 잔액 불일치 확인
                // 잔액이 맞지 않으면 롤백 처리 (여기서는 예외를 던져서 롤백을 유도)
                throw new Exception("잔액 불일치로 충전 롤백");
            }

            ChargeResponse response = new ChargeResponse(updatedBalance);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            // 서버 오류 또는 잔액 불일치로 인한 롤백 처리
            return new ResponseEntity<>(new ErrorResponse("500", "서버 오류로 인해 잔액 충전에 실패했습니다."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean checkUserExists(Long userId) {
        // 유저 존재 여부 확인 로직 구현
        // 유저가 존재하면 true, 그렇지 않으면 false 반환
        return true; // 예시로 유저가 존재하는 경우를 반환
    }

    private double getCurrentBalance(Long userId) {
        // 현재 잔액 조회 로직 구현
        return 5000.00; // 예시로 현재 잔액을 반환
    }

    private double chargeUserBalance(Long userId, Double amount) {
        // 유저의 잔액 충전 로직 구현
        // 현재 잔액을 반환
        return 5000.00 + amount; // 예시로 잔액을 충전한 후의 값을 반환
    }
}


