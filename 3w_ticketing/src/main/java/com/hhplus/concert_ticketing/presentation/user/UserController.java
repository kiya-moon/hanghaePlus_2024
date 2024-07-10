package com.hhplus.concert_ticketing.presentation.user;

import com.hhplus.concert_ticketing.presentation.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {
    @PatchMapping("/balance/charge")
    public ResponseEntity<?> chargeBalance(@RequestBody ChargeRequest request) {
        if (request.getUserId() == null || request.getAmount() == null || request.getAmount() <= 0) {
            return new ResponseEntity<>(new ErrorResponse("400", "값이 유효하지 않습니다. 관리자에게 문의해주세요."), HttpStatus.BAD_REQUEST);
        }

        // 충전 처리 로직
        ChargeResponse response = new ChargeResponse(5000.00);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/balance")
    public ResponseEntity<?> getBalance(@RequestParam Long userId) {
        if (userId == null) {
            return new ResponseEntity<>(new ErrorResponse("400", "값이 유효하지 않습니다. 관리자에게 문의해주세요."), HttpStatus.BAD_REQUEST);
        }

        // 잔액 조회 로직
        BalanceResponse response = new BalanceResponse(5000.00);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
