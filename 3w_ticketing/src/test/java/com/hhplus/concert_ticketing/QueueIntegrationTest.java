package com.hhplus.concert_ticketing;

import com.hhplus.concert_ticketing.application.TokenFacade;
import com.hhplus.concert_ticketing.domain.queue.TokenStatus;
import com.hhplus.concert_ticketing.domain.queue.QueueService;
import com.hhplus.concert_ticketing.domain.queue.QueueRepository;
import com.hhplus.concert_ticketing.presentation.queue.QueueController;
import com.hhplus.concert_ticketing.presentation.queue.TokenData;
import com.hhplus.concert_ticketing.presentation.queue.TokenResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class QueueIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private TokenFacade tokenFacade;

    @InjectMocks
    private QueueController queueController;

    @Mock
    private QueueService queueService;

    @Mock
    private QueueRepository queueRepository;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(queueController).build();
    }

    @Test
    public void testCheckQueue_whenTokenStatusIsWaiting() throws Exception {
        String token = "dummyToken";
        TokenResponse tokenResponse = new TokenResponse("100", "계속 대기 중입니다.", new TokenData(token, 1, "2024-07-04T12:00:00"));
        when(tokenFacade.checkTokenStatus(anyString())).thenReturn(TokenStatus.WAITING);

        mockMvc.perform(get("/queue/check-queue")
                        .param("token", token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isContinue())
                .andExpect(jsonPath("$.result").value("100"))
                .andExpect(jsonPath("$.message").value("계속 대기 중입니다."))
                .andExpect(jsonPath("$.data.token").value(token));
    }

    @Test
    public void testCheckQueue_whenTokenStatusIsActive() throws Exception {
        String token = "dummyToken";
        when(tokenFacade.checkTokenStatus(anyString())).thenReturn(TokenStatus.ACTIVE);

        mockMvc.perform(get("/queue/check-queue")
                        .param("token", token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isSeeOther())
                .andExpect(header().string(HttpHeaders.LOCATION, "/reservation-page"));
    }

    @Test
    public void testCheckQueue_whenTokenStatusIsInvalid() throws Exception {
        String token = "dummyToken";
        when(tokenFacade.checkTokenStatus(anyString())).thenThrow(new RuntimeException("잘못된 토큰 상태"));

        mockMvc.perform(get("/queue/check-queue")
                        .param("token", token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("400"))
                .andExpect(jsonPath("$.errorMessage").value("잘못된 토큰 상태입니다."));
    }

    @Test
    public void testCheckQueue_whenTokenStatusThrowsException() throws Exception {
        String token = "dummyToken";
        when(tokenFacade.checkTokenStatus(anyString())).thenThrow(new RuntimeException("서버 오류"));

        mockMvc.perform(get("/queue/check-queue")
                        .param("token", token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorCode").value("500"))
                .andExpect(jsonPath("$.errorMessage").value("서버에서 오류가 발생했습니다."));
    }
}
