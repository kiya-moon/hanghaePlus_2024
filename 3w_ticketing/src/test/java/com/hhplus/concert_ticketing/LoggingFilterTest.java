package com.hhplus.concert_ticketing;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class LoggingFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void 로깅_필터_테스트() throws Exception {
        mockMvc.perform(get("/api/get-concerts"))
                .andExpect(status().isOk());
    }
}

