package com.hhplus.concert_ticketing.presentation;

import com.hhplus.concert_ticketing.presentation.concert.AvailableDatesResponse;
import com.hhplus.concert_ticketing.presentation.concert.AvailableSeatsResponse;
import com.hhplus.concert_ticketing.presentation.concert.ConcertOption;
import com.hhplus.concert_ticketing.presentation.concert.Seat;
import com.hhplus.concert_ticketing.presentation.reservation.*;
import com.hhplus.concert_ticketing.presentation.user.BalanceResponse;
import com.hhplus.concert_ticketing.presentation.user.ChargeRequest;
import com.hhplus.concert_ticketing.presentation.queue.TokenData;
import com.hhplus.concert_ticketing.presentation.queue.TokenRequest;
import com.hhplus.concert_ticketing.presentation.queue.TokenResponse;
import com.hhplus.concert_ticketing.presentation.user.ChargeResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

//@RestController
public class TicketingMockApiController {

    @PostMapping("/token")
    public ResponseEntity<TokenResponse> issueToken(@RequestBody TokenRequest request) {
        Long userId = request.getUserId();

        if (userId == null) {
            return new ResponseEntity<>(new TokenResponse("400", "값이 유효하지 않습니다. 관리자에게 문의해주세요.", null), HttpStatus.BAD_REQUEST);
        }

        // token 새로 생성
        String token = UUID.randomUUID().toString();

        TokenResponse response = new TokenResponse("200", "Success", new TokenData(token, 1, "2024-07-04T12:00:00"));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/api/{concertId}/available-dates")
    public ResponseEntity<?> getAvailableDates(
            @PathVariable Long concertId,
            @RequestParam String token) {

        if (token == null || token.isEmpty()) {
            return new ResponseEntity<>(new ErrorResponse("401", "대기시간이 초과되었습니다."), HttpStatus.UNAUTHORIZED);
        }

        AvailableDatesResponse response = new AvailableDatesResponse(List.of(
                new ConcertOption(1L, "2024-07-04"),
                new ConcertOption(2L, "2024-07-05")
        ));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/api/{concertOptionId}/available-seats")
    public ResponseEntity<?> getAvailableSeats(
            @PathVariable Long concertOptionId,
            @RequestParam String token) {

        if (token == null || token.isEmpty()) {
            return new ResponseEntity<>(new ErrorResponse("401", "대기시간이 초과되었습니다."), HttpStatus.UNAUTHORIZED);
        }

        AvailableSeatsResponse response = new AvailableSeatsResponse(List.of(
                new Seat(1L, "A1", "열림"),
                new Seat(2L, "A2", "열림")
        ));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/api/reserve")
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

    @PatchMapping("/api/balance/charge")
    public ResponseEntity<?> chargeBalance(@RequestBody ChargeRequest request) {
        if (request.getUserId() == null || request.getAmount() == null || request.getAmount() <= 0) {
            return new ResponseEntity<>(new ErrorResponse("400", "값이 유효하지 않습니다. 관리자에게 문의해주세요."), HttpStatus.BAD_REQUEST);
        }

        // 충전 처리 로직
        ChargeResponse response = new ChargeResponse(5000.00);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/api/balance")
    public ResponseEntity<?> getBalance(@RequestParam Long userId) {
        if (userId == null) {
            return new ResponseEntity<>(new ErrorResponse("400", "값이 유효하지 않습니다. 관리자에게 문의해주세요."), HttpStatus.BAD_REQUEST);
        }

        // 잔액 조회 로직
        BalanceResponse response = new BalanceResponse(5000.00);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/api/pay")
    public ResponseEntity<?> pay(@RequestBody PayRequest request) {
        if (request.getReservationId() == null || request.getAmount() == null || request.getAmount() <= 0) {
            return new ResponseEntity<>(new ErrorResponse("400", "값이 유효하지 않습니다. 관리자에게 문의해주세요."), HttpStatus.BAD_REQUEST);
        }

        PayResponse response = new PayResponse("200", "Success", new PayData(456L));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
