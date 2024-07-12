package com.hhplus.concert_ticketing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhplus.concert_ticketing.application.UserFacade;
import com.hhplus.concert_ticketing.presentation.user.ChargeRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserFacade userFacade;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void getBalance_Successful() throws Exception {
        Long userId = 1L;
        Double balance = 100.0;

        when(userFacade.getBalance(userId)).thenReturn(balance);

        mockMvc.perform(get("/api/balance")
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(balance));

        verify(userFacade, times(1)).getBalance(anyLong());
    }

    @Test
    void getBalance_UserNotFound() throws Exception {
        Long userId = 1L;

        when(userFacade.getBalance(userId)).thenThrow(new NoSuchElementException("접근이 유효하지 않습니다."));

        mockMvc.perform(get("/api/balance")
                        .param("userId", userId.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result").value("401"))
                .andExpect(jsonPath("$.message").value("접근이 유효하지 않습니다."));

        verify(userFacade, times(1)).getBalance(anyLong());
    }

    @Test
    void chargeBalance_Successful() throws Exception {
        Long userId = 1L;
        Double amount = 50.0;
        Double newBalance = 150.0;
        ChargeRequest request = new ChargeRequest(userId, amount);

        when(userFacade.chargePoint(userId, amount)).thenReturn(newBalance);

        mockMvc.perform(patch("/api/balance/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.newBalance").value(newBalance));

        verify(userFacade, times(1)).chargePoint(anyLong(), any(Double.class));
    }

    @Test
    void chargeBalance_InvalidAmount() throws Exception {
        Long userId = 1L;
        ChargeRequest request = new ChargeRequest(userId, -50.0);

        mockMvc.perform(patch("/api/balance/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value("400"))
                .andExpect(jsonPath("$.message").value("값이 유효하지 않습니다. 관리자에게 문의해주세요."));

        verify(userFacade, times(0)).chargePoint(anyLong(), any(Double.class));
    }

    @Test
    void chargeBalance_UserNotFound() throws Exception {
        Long userId = 1L;
        Double amount = 50.0;
        ChargeRequest request = new ChargeRequest(userId, amount);

        when(userFacade.chargePoint(userId, amount)).thenThrow(new NoSuchElementException("접근이 유효하지 않습니다."));

        mockMvc.perform(patch("/api/balance/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result").value("401"))
                .andExpect(jsonPath("$.message").value("접근이 유효하지 않습니다."));

        verify(userFacade, times(1)).chargePoint(anyLong(), any(Double.class));
    }

    @Test
    void chargeBalance_ServerError() throws Exception {
        Long userId = 1L;
        Double amount = 50.0;
        ChargeRequest request = new ChargeRequest(userId, amount);

        when(userFacade.chargePoint(userId, amount)).thenThrow(new RuntimeException("서버 오류로 인해 잔액 충전에 실패했습니다."));

        mockMvc.perform(patch("/api/balance/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.result").value("500"))
                .andExpect(jsonPath("$.message").value("서버 오류로 인해 잔액 충전에 실패했습니다."));

        verify(userFacade, times(1)).chargePoint(anyLong(), any(Double.class));
    }
}
