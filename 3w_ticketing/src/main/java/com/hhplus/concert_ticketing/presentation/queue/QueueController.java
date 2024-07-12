package com.hhplus.concert_ticketing.presentation.queue;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/queue")
@Tag(name = "queue", description = "대기열 관련 API")
public class QueueController {
    @PostMapping("/issue-token")
    @Operation(
            summary = "토큰 발급",
            description = "랜덤 UUID를 기반으로 토큰을 발급합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공적으로 토큰을 발급했습니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponse.class))
                    ),
                    @ApiResponse(responseCode = "401", description = "접근이 유효하지 않습니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    ),
            }
    )
    public ResponseEntity<TokenResponse> issueToken(@RequestBody TokenRequest request) {
        Long userId = request.getUserId();

        if (userId == null) {
            return new ResponseEntity<>(new TokenResponse("401", "접근이 유효하지 않습니다.", null), HttpStatus.UNAUTHORIZED);
        }

        // token 새로 생성
        // token에 추가적인 정보가 필요할 경우 UUID + / + concertId or userId 등으로 조합해서 생성 가능
        // concertId 등은 필요시 리퀘스트 파람 수정해서 받아올 수 있음
        String token = UUID.randomUUID().toString();

        TokenResponse response = new TokenResponse("200", "Success", new TokenData(token, 1, "2024-07-04T12:00:00"));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/check-queue")
    @Operation(
            summary = "대기열 체크",
            description = "발급된 토큰 정보로 대기열을 체크합니다.",
            responses = {
                    @ApiResponse(responseCode = "100", description = "계속 대기 합니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponse.class))
                    ),
                    @ApiResponse(responseCode = "303", description = "토큰 상태가 'ACTIVE'인 경우 리다이렉션",
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
            }
    )
    public ResponseEntity<TokenResponse> checkQueue(@RequestParam String token) {
        if (token == null || token.isEmpty()) {
            return new ResponseEntity<>(new TokenResponse("401", "접근이 유효하지 않습니다.", null), HttpStatus.UNAUTHORIZED);
        }

        String tokenStatus = getTokenStatus(token);  // WAITING, ACTIVE, EXPIRED

        if ("ACTIVE".equals(tokenStatus)) {
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create("/reservation-page"));
            return new ResponseEntity<>(headers, HttpStatus.SEE_OTHER);
        } else {
            TokenResponse response = new TokenResponse("100", "Continue", new TokenData(token, 1, "2024-07-04T12:00:00"));
            return new ResponseEntity<>(response, HttpStatus.CONTINUE);
        }
    }

    private String getTokenStatus(String token) {
        if ("active_token".equals(token)) {
            return "ACTIVE";
        }
        return "WAITING";
    }
}
