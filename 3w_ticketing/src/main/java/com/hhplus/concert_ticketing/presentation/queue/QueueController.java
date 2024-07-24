package com.hhplus.concert_ticketing.presentation.queue;

import com.hhplus.concert_ticketing.application.QueueFacade;
import com.hhplus.concert_ticketing.domain.queue.QueueMapper;
import com.hhplus.concert_ticketing.presentation.ErrorResponse;
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
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/queue")
@RequiredArgsConstructor
@Tag(name = "queue", description = "대기열 관련 API")
public class QueueController {

    private final QueueFacade queueFacade;

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
                    @ApiResponse(responseCode = "500", description = "서버 오류로 인해 토큰 발급에 실패했습니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    public ResponseEntity<?> issueToken(@RequestBody TokenRequest request) {
        try {
            String token = queueFacade.requestToken(request.getUserId());
            TokenDto tokenDto = new TokenDto(token, 1, "2024-07-04T12:00:00", 0);
            TokenResponse response = new TokenResponse("200", "Success", tokenDto);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            ErrorResponse errorResponse = new ErrorResponse("401", "접근이 유효하지 않습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse("500", "서버에서 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
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
                    @ApiResponse(responseCode = "400", description = "잘못된 토큰 상태입니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(responseCode = "500", description = "서버 오류로 인해 대기열 체크에 실패했습니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    public ResponseEntity<?> checkQueue(@RequestParam String token) {
        try {
            TokenResponse response = queueFacade.checkTokenStatus(token);

            if (response.getResult().equals("200")) {
                HttpHeaders headers = new HttpHeaders();
                headers.setLocation(URI.create("/reservation-page"));
                return new ResponseEntity<>(headers, HttpStatus.SEE_OTHER);
            } else if (response.getResult().equals("100")) {
                return ResponseEntity.status(HttpStatus.CONTINUE).body(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(QueueMapper.toErrorResponseDTO(response.getResult(), response.getMessage()));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(QueueMapper.toErrorResponseDTO("400", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(QueueMapper.toErrorResponseDTO("500", "서버에서 오류가 발생했습니다."));
        }
    }
}
