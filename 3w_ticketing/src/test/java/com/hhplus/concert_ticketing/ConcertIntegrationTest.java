package com.hhplus.concert_ticketing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhplus.concert_ticketing.application.ConcertFacade;
import com.hhplus.concert_ticketing.domain.concert.SeatEntity;
import com.hhplus.concert_ticketing.domain.concert.SeatStatus;
import com.hhplus.concert_ticketing.presentation.ErrorResponse;
import com.hhplus.concert_ticketing.presentation.concert.Seat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
public class ConcertControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConcertFacade concertFacade;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Mocking ConcertFacade's getAvailableSeats method
        when(concertFacade.getAvailableSeats(anyLong(), anyString())).thenReturn(List.of(
                new Seat(1L, 1L, "A1", SeatStatus.UNLOCKED.toString()),
                new Seat(2L, 1L, "A2", SeatStatus.UNLOCKED.toString())
        ));
    }

    @Test
    public void testGetAvailableSeats() throws Exception {
        Long concertOptionId = 1L;
        String token = "valid_token";

        mockMvc.perform(MockMvcRequestBuilders.get("/api/{concertOptionId}/available-seats", concertOptionId)
                        .param("token", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].seatNumber").value("A1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].seatNumber").value("A2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[0].status").value("UNLOCKED"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].status").value("UNLOCKED"));
    }

    @Test
    public void testGetAvailableSeatsWithInvalidToken() throws Exception {
        Long concertOptionId = 1L;
        String token = "invalid_token";

        // Throwing IllegalArgumentException to test the UNAUTHORIZED response
        when(concertFacade.getAvailableSeats(anyLong(), anyString())).thenThrow(new IllegalArgumentException("유효하지 않은 접근입니다."));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/{concertOptionId}/available-seats", concertOptionId)
                        .param("token", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value("401"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("접근이 유효하지 않습니다."));
    }

    @Test
    public void testGetAvailableSeatsWithException() throws Exception {
        Long concertOptionId = 1L;
        String token = "valid_token";

        // Throwing a general Exception to test the FORBIDDEN response
        when(concertFacade.getAvailableSeats(anyLong(), anyString())).thenThrow(new RuntimeException("대기시간이 만료되었습니다."));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/{concertOptionId}/available-seats", concertOptionId)
                        .param("token", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result").value("403"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("대기시간이 만료되었습니다."));
    }
}
