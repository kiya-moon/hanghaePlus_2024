package com.hhplus.concert_ticketing;

import com.hhplus.concert_ticketing.application.QueueFacade;
import com.hhplus.concert_ticketing.presentation.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hhplus.concert_ticketing.presentation.queue.QueueController;
import com.hhplus.concert_ticketing.presentation.queue.TokenData;
import com.hhplus.concert_ticketing.presentation.queue.TokenRequest;
import com.hhplus.concert_ticketing.presentation.queue.TokenResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
class QueueControllerIntegrationTest {

    @Autowired
    private QueueController queueController;

    @Autowired
    private QueueFacade queueFacade;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void issueToken_성공() throws Exception {
        // given
        TokenRequest request = new TokenRequest(123L); // userId를 long 타입으로 설정
        String token = "some-unique-token";
        TokenData tokenData = new TokenData(token, 1, "2024-07-04T12:00:00", 0);
        TokenResponse expectedResponse = new TokenResponse("200", "Success", tokenData);

        when(queueFacade.requestToken(anyLong())).thenReturn(token);

        // when
        ResponseEntity<?> result = queueController.issueToken(request);

        // then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(objectMapper.writeValueAsString(expectedResponse), result.getBody());
    }

    @Test
    void issueToken_실패() throws Exception {
        // given
        TokenRequest request = new TokenRequest(123L); // userId를 long 타입으로 설정

        when(queueFacade.requestToken(anyLong())).thenThrow(new RuntimeException("접근이 유효하지 않습니다."));

        // when
        ResponseEntity<?> result = queueController.issueToken(request);

        // then
        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        ErrorResponse errorResponse = new ErrorResponse("401", "접근이 유효하지 않습니다.");
        assertEquals(objectMapper.writeValueAsString(errorResponse), result.getBody());
    }

    @Test
    void checkQueue_성공_리다이렉션() throws Exception {
        // given
        String token = "valid-token";
        TokenResponse tokenResponse = new TokenResponse("200", "Success", null);

        when(queueFacade.checkTokenStatus(token)).thenReturn(tokenResponse);

        // when
        ResponseEntity<?> result = queueController.checkQueue(token);

        // then
        assertEquals(HttpStatus.SEE_OTHER, result.getStatusCode());
        assertEquals("/reservation-page", result.getHeaders().getLocation().toString());
    }

    @Test
    void checkQueue_성공_대기() throws Exception {
        // given
        String token = "waiting-token";
        TokenResponse tokenResponse = new TokenResponse("100", "Waiting", null);

        when(queueFacade.checkTokenStatus(token)).thenReturn(tokenResponse);

        // when
        ResponseEntity<?> result = queueController.checkQueue(token);

        // then
        assertEquals(HttpStatus.CONTINUE, result.getStatusCode());
        assertEquals(objectMapper.writeValueAsString(tokenResponse), result.getBody());
    }

    @Test
    void checkQueue_실패_잘못된_토큰() throws Exception {
        // given
        String token = "invalid-token";

        when(queueFacade.checkTokenStatus(token)).thenThrow(new IllegalArgumentException("잘못된 토큰 상태입니다."));

        // when
        ResponseEntity<?> result = queueController.checkQueue(token);

        // then
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        ErrorResponse errorResponse = new ErrorResponse("400", "잘못된 토큰 상태입니다.");
        assertEquals(objectMapper.writeValueAsString(errorResponse), result.getBody());
    }

    @Test
    void checkQueue_실패_서버_오류() throws Exception {
        // given
        String token = "some-token";

        when(queueFacade.checkTokenStatus(token)).thenThrow(new RuntimeException("서버에서 오류가 발생했습니다."));

        // when
        ResponseEntity<?> result = queueController.checkQueue(token);

        // then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        ErrorResponse errorResponse = new ErrorResponse("500", "서버에서 오류가 발생했습니다.");
        assertEquals(objectMapper.writeValueAsString(errorResponse), result.getBody());
    }
}
