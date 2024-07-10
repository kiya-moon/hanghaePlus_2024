package com.hhplus.concert_ticketing.presentation.reservation;

import com.hhplus.concert_ticketing.presentation.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ReservationController {
    @PostMapping("/reserve")
    public ResponseEntity<?> reserveSeat(@RequestBody ReserveRequest request) {
        if (request.getToken() == null || request.getToken().isEmpty()) {
            return new ResponseEntity<>(new ErrorResponse("401", "대기시간이 초과되었습니다."), HttpStatus.UNAUTHORIZED);
        }

        if (request.getConcertOptionId() == null || request.getSeatId() == null || request.getUserId() == null) {
            return new ResponseEntity<>(new ErrorResponse("400", "값이 유효하지 않습니다. 관리자에게 문의해주세요."), HttpStatus.BAD_REQUEST);
        }

        ReserveResponse response = new ReserveResponse("200", "Success", new ReserveData(123L));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/pay")
    public ResponseEntity<?> pay(@RequestBody PayRequest request) {
        if (request.getToken() == null || request.getToken().isEmpty()) {
            return new ResponseEntity<>(new ErrorResponse("401", "대기시간이 초과되었습니다."), HttpStatus.UNAUTHORIZED);
        }

        if (request.getReservationId() == null || request.getAmount() == null || request.getAmount() <= 0) {
            return new ResponseEntity<>(new ErrorResponse("400", "값이 유효하지 않습니다. 관리자에게 문의해주세요."), HttpStatus.BAD_REQUEST);
        }

        PayResponse response = new PayResponse("200", "Success", new PayData(456L));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
