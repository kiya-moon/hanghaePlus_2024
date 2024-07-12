package com.hhplus.concert_ticketing;

import com.hhplus.concert_ticketing.domain.queue.TokenEntity;
import com.hhplus.concert_ticketing.domain.queue.TokenStatus;
import com.hhplus.concert_ticketing.application.TokenFacade;
import com.hhplus.concert_ticketing.presentation.queue.QueueController;
import com.hhplus.concert_ticketing.domain.queue.QueueService;
import com.hhplus.concert_ticketing.domain.queue.QueueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class QueueControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private TokenFacade tokenFacade;

    @InjectMocks
    private QueueController QueueController;

    @Mock
    private QueueService queueService;

    @Mock
    private QueueRepository queueRepository;

    @BeforeEach
    public void setup() {
        // Create the MockMvc instance
        mockMvc = MockMvcBuilders.standaloneSetup(QueueController).build();
    }

    @Test
    public void testCheckQueueWithActiveToken() throws Exception {
        String token = "active_token";
        TokenEntity tokenEntity = TokenEntity.builder()
                .id(1L)
                .userId(1L)
                .token(token)
                .status(TokenStatus.ACTIVE)
                .createdAt(Timestamp.from(Instant.now()))
                .expiresAt(Timestamp.from(Instant.now().plusSeconds(3600)))
                .build();

        when(queueRepository.findByToken(token)).thenReturn(Optional.of(tokenEntity));
        when(tokenFacade.checkTokenStatus(anyString())).thenReturn(TokenStatus.ACTIVE);

        mockMvc.perform(get("/queue/check-queue")
                        .param("token", token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isSeeOther())
                .andExpect(result -> {
                    assertTrue(result.getResponse().getHeader(HttpHeaders.LOCATION).endsWith("/reservation-page"));
                });
    }

    @Test
    public void testCheckQueueWithWaitingToken() throws Exception {
        String token = "waiting_token";
        TokenEntity tokenEntity = TokenEntity.builder()
                .id(2L)
                .userId(2L)
                .token(token)
                .status(TokenStatus.WAITING)
                .createdAt(Timestamp.from(Instant.now()))
                .expiresAt(Timestamp.from(Instant.now().plusSeconds(3600)))
                .build();
        when(queueRepository.findByToken(token)).thenReturn(Optional.of(tokenEntity));
        when(tokenFacade.checkTokenStatus(anyString())).thenReturn(TokenStatus.WAITING);

        mockMvc.perform(get("/queue/check-queue")
                        .param("token", token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isContinue())
                .andExpect(jsonPath("$.code").value("100"))
                .andExpect(jsonPath("$.message").value("계속 대기 중입니다."))
                .andExpect(jsonPath("$.data.token").value(token))
                .andExpect(jsonPath("$.data.concertId").value(1))
                .andExpect(jsonPath("$.data.expiryDate").value("2024-07-04T12:00:00"));
    }

    @Test
    public void testCheckQueueWithInvalidToken() throws Exception {
        String token = "invalid_token";
        TokenEntity tokenEntity = TokenEntity.builder()
                .id(3L)
                .userId(3L)
                .token(token)
                .status(TokenStatus.EXPIRED)
                .createdAt(Timestamp.from(Instant.now()))
                .expiresAt(Timestamp.from(Instant.now().minusSeconds(3600)))  // 과거의 만료일 설정
                .build();

        when(queueRepository.findByToken(token)).thenReturn(Optional.of(tokenEntity));
        when(tokenFacade.checkTokenStatus(anyString())).thenReturn(TokenStatus.EXPIRED);

        mockMvc.perform(get("/queue/check-queue")
                        .param("token", token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 토큰 상태입니다."));
    }

    @Test
    public void testCheckQueueWithException() throws Exception {
        String token = "some_token";

        when(tokenFacade.checkTokenStatus(anyString())).thenThrow(new RuntimeException("Server error"));

        mockMvc.perform(get("/queue/check-queue")
                        .param("token", token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.result").value("500"))
                .andExpect(jsonPath("$.message").value("서버에서 오류가 발생했습니다."));
    }
}
