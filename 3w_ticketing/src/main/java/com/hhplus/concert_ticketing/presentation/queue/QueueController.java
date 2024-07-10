package com.hhplus.concert_ticketing.presentation.queue;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/queue")
public class QueueController {
    @PostMapping("/issue-token")
    public ResponseEntity<TokenResponse> issueToken(@RequestBody TokenRequest request) {
        Long userId = request.getUserId();
        Long concertId = request.getConcertId();
        String token = request.getToken();

        if (userId == null || concertId == null) {
            return new ResponseEntity<>(new TokenResponse("400", "값이 유효하지 않습니다. 관리자에게 문의해주세요.", null), HttpStatus.BAD_REQUEST);
        }

        // token이 없으면 새로 생성
        if (token == null) {
            token = UUID.randomUUID().toString() + "/" + concertId;
        }

        TokenResponse response = new TokenResponse("200", "Success", new TokenData(token, 1, "2024-07-04T12:00:00"));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/check-queue")
    public ResponseEntity<TokenResponse> checkQueue(@RequestParam String token) {
        if (token == null || token.isEmpty()) {
            return new ResponseEntity<>(new TokenResponse("401", "대기시간이 초과되었습니다.", null), HttpStatus.UNAUTHORIZED);
        }

        String tokenStatus = getTokenStatus(token);  // WAITING, ACTIVE, EXPIRED

        if ("ACTIVE".equals(tokenStatus)) {
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create("/reservation-page"));
            return new ResponseEntity<>(headers, HttpStatus.SEE_OTHER);
        } else {
            TokenResponse response = new TokenResponse("200", "Success", new TokenData(token, 1, "2024-07-04T12:00:00"));
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

    }

    private String getTokenStatus(String token) {
        if ("active_token".equals(token)) {
            return "ACTIVE";
        }
        return "WAITING";
    }
}
