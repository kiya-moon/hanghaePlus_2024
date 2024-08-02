package com.hhplus.concert_ticketing.presentation.queue;

import com.hhplus.concert_ticketing.application.QueueFacade;
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

@RestController
@RequestMapping("/queue")
@RequiredArgsConstructor
@Tag(name = "queue", description = "대기열 관련 API")
public class QueueController {

    private static final Logger logger = LoggerFactory.getLogger(QueueController.class);
    private final QueueFacade queueFacade;

    @PostMapping("/issue-token")
    @Operation(
            summary = "토큰 발급",
            description = "랜덤 UUID를 기반으로 토큰을 발급합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공적으로 토큰을 발급했습니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponse.class))
                    ),
                    @ApiResponse(responseCode = "500", description = "서버 오류로 인해 토큰 발급에 실패했습니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    public ResponseEntity<Object> issueToken(@RequestBody TokenRequest request) {
        try {
            String token = queueFacade.requestToken(request.getUserId());
            TokenResponse response = new TokenResponse("200", "토큰이 발급되었습니다.", token, 0);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("토큰 발급 중 오류 발생. 사용자 ID: {}, 오류 메시지: {}", request.getUserId(), e.getMessage());
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
                    @ApiResponse(responseCode = "200", description = "토큰 상태가 'ACTIVE'인 경우",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponse.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "잘못된 토큰 상태입니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    ),
                    @ApiResponse(responseCode = "500", description = "서버 오류로 인해 대기열 체크에 실패했습니다.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
    public ResponseEntity<Object> checkQueue(@RequestParam String token) {
        try {
            TokenResponse response = queueFacade.checkTokenStatus(token);

            if ("200".equals(response.getResult())) {
                return ResponseEntity.ok(response);
            } else if ("100".equals(response.getResult())) {
                return ResponseEntity.status(HttpStatus.CONTINUE).body(response);
            } else {
                logger.warn("잘못된 토큰 상태입니다. 상태: {}, 메시지: {}", response.getResult(), response.getMessage());
                ErrorResponse errorResponse = new ErrorResponse("400", response.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
        } catch (Exception e) {
            logger.error("서버 오류로 대기열 체크 실패. 토큰: {}, 오류 메시지: {}", token, e.getMessage());
            ErrorResponse errorResponse = new ErrorResponse("500", "서버에서 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}

