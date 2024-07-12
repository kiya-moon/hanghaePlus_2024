//package com.hhplus.concert_ticketing.presentation;
//
//import com.hhplus.concert_ticketing.presentation.user.ChargeRequest;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(TicketingMockApiController.class)
//public class TicketingControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Test
//    public void testIssueToken() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.post("/token")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{ \"userId\": 1, \"concertId\": 1, \"token\": null }"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.result").value("200"))
//                .andExpect(jsonPath("$.data.token").isNotEmpty());
//    }
//
//
//    @Test
//    public void testGetAvailableDates() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/1/available-dates")
//                        .param("token", "test-token"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.concertOptions").isArray());
//    }
//
//
//    @Test
//    public void testGetAvailableSeats() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/1/available-seats")
//                        .param("token", "test-token"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.availableSeats").isArray());
//    }
//
//
//    @Test
//    public void testReserveSeat() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/reserve")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{ \"token\": \"test-token\", \"concertOptionId\": 1, \"seatId\": 1, \"userId\": 1 }"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.result").value("200"))
//                .andExpect(jsonPath("$.data.reservationId").value(123));
//    }
//
//
//    @Test
//    public void testChargeBalance() throws Exception {
//        ChargeRequest chargeRequest = new ChargeRequest(1L, 5000.00);
//
//        mockMvc.perform(MockMvcRequestBuilders.patch("/api/balance/charge")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{ \"userId\": 1, \"amount\": 5000 }"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.balance").value(5000.00));
//    }
//
//    @Test
//    public void testPay() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/pay")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{ \"token\": \"test-token\", \"reservationId\": 123, \"amount\": 1000 }"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.result").value("200"))
//                .andExpect(jsonPath("$.data.paymentId").value(456));
//    }
//
//}
//
