package com.hhplus.concert_ticketing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhplus.concert_ticketing.application.ReservationFacade;
import com.hhplus.concert_ticketing.domain.concert.ConcertService;
import com.hhplus.concert_ticketing.domain.concert.SeatEntity;
import com.hhplus.concert_ticketing.domain.concert.SeatRepository;
import com.hhplus.concert_ticketing.domain.concert.SeatStatus;
import com.hhplus.concert_ticketing.domain.queue.QueueService;
import com.hhplus.concert_ticketing.domain.reservation.ReservationEntity;
import com.hhplus.concert_ticketing.domain.reservation.ReservationService;
import com.hhplus.concert_ticketing.domain.user.UserEntity;
import com.hhplus.concert_ticketing.domain.user.UserRepository;
import com.hhplus.concert_ticketing.presentation.reservation.ReserveRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.sql.Timestamp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ReservationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationFacade reservationFacade;

    @MockBean
    private SeatRepository seatRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private QueueService queueService;

    @MockBean
    private ConcertService concertService;

    @MockBean
    private ReservationService reservationService;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void reserveSeat_SuccessfulReservation() throws Exception {
        String token = "valid-token";
        Long seatId = 1L;
        Long userId = 1L;
        Long concertOptionId = 1L;

        SeatEntity seat = new SeatEntity();
        seat.setId(seatId);
        seat.setStatus(SeatStatus.UNLOCKED);

        when(concertService.getSeatStatus(seatId)).thenReturn(SeatStatus.UNLOCKED);
        when(queueService.checkTokenValidity(token)).thenReturn(true);
        doNothing().when(reservationFacade).reserveSeat(token, seatId, userId);

        ReserveRequest request = new ReserveRequest(token, concertOptionId, seatId, userId);

        mockMvc.perform(post("/api/reserve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isSeeOther())
                .andExpect(MockMvcResultMatchers.header().string(HttpHeaders.LOCATION, "/payment-page"))
                .andExpect(MockMvcResultMatchers.content().string(""));

        verify(reservationFacade, times(1)).reserveSeat(token, seatId, userId);
    }

    @Test
    void reserveSeat_TokenExpired() throws Exception {
        String token = "expired-token";
        Long seatId = 1L;
        Long userId = 1L;
        Long concertOptionId = 1L;

        when(queueService.checkTokenValidity(token)).thenReturn(false);

        ReserveRequest request = new ReserveRequest(token, concertOptionId, seatId, userId);

        mockMvc.perform(post("/api/reserve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.result").value("400"))
                .andExpect(jsonPath("$.message").value("토큰이 만료되었습니다."));
    }

    @Test
    void reserveSeat_SeatAlreadyLocked() throws Exception {
        String token = "valid-token";
        Long seatId = 1L;
        Long userId = 1L;
        Long concertOptionId = 1L;

        when(concertService.getSeatStatus(seatId)).thenReturn(SeatStatus.LOCKED);  // Seat status is LOCKED
        when(queueService.checkTokenValidity(token)).thenReturn(true);

        ReserveRequest request = new ReserveRequest(token, concertOptionId, seatId, userId);

        mockMvc.perform(post("/api/reserve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.result").value("403"))
                .andExpect(jsonPath("$.message").value("이미 예매된 좌석입니다."));
    }

    @Test
    void reserveSeat_OtherExceptionHandling() throws Exception {
        String token = "valid-token";
        Long seatId = 1L;
        Long userId = 1L;
        Long concertOptionId = 1L;

        when(concertService.getSeatStatus(seatId)).thenThrow(new RuntimeException("Unexpected error occurred"));
        when(queueService.checkTokenValidity(token)).thenReturn(true);

        ReserveRequest request = new ReserveRequest(token, concertOptionId, seatId, userId);

        mockMvc.perform(post("/api/reserve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.result").value("500"))
                .andExpect(jsonPath("$.message").value("예기치 않은 오류가 발생했습니다."));

        verify(reservationFacade, times(0)).reserveSeat(token, seatId, userId);
    }

    @Test
    void requestPayment_SuccessfulPayment() throws Exception {
        Long reservationId = 1L;

        ReservationEntity reservation = new ReservationEntity();
        reservation.setId(reservationId);
        reservation.setPrice(100.0);
        reservation.setExpiredAt(new Timestamp(System.currentTimeMillis() + 60000));
        reservation.setStatus("PENDING");

        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setBalance(200.0);  // Sufficient balance

        when(reservationService.getReservation(anyLong())).thenReturn(reservation);
        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.of(user));
        doNothing().when(reservationFacade).requestPayment(reservationId);

        mockMvc.perform(post("/api/payment/{reservationId}", reservationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("결제 성공"));

        verify(reservationFacade, times(1)).requestPayment(reservationId);
    }

    @Test
    void requestPayment_ReservationExpired() throws Exception {
        Long reservationId = 1L;

        ReservationEntity reservation = new ReservationEntity();
        reservation.setId(reservationId);
        reservation.setPrice(100.0);
        reservation.setExpiredAt(new Timestamp(System.currentTimeMillis() - 60000));  // Past due date
        reservation.setStatus("PENDING");

        when(reservationService.getReservation(anyLong())).thenReturn(reservation);

        mockMvc.perform(post("/api/payment/{reservationId}", reservationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.result").value("409"))
                .andExpect(jsonPath("$.message").value("결제 시간이 만료되었습니다."));
    }

    @Test
    void requestPayment_InsufficientBalance() throws Exception {
        Long reservationId = 1L;

        ReservationEntity reservation = new ReservationEntity();
        reservation.setId(reservationId);
        reservation.setPrice(100.0);
        reservation.setExpiredAt(new Timestamp(System.currentTimeMillis() + 60000));
        reservation.setStatus("PENDING");

        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setBalance(50.0);  // Insufficient balance

        when(reservationService.getReservation(anyLong())).thenReturn(reservation);
        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.of(user));

        mockMvc.perform(post("/api/payment/{reservationId}", reservationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isPaymentRequired())
                .andExpect(jsonPath("$.result").value("402"))
                .andExpect(jsonPath("$.message").value("잔액이 부족합니다."));
    }
}
