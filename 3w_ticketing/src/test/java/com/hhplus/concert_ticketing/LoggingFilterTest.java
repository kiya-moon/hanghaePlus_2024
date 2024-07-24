package com.hhplus.concert_ticketing;

import com.hhplus.concert_ticketing.application.ConcertFacade;
import com.hhplus.concert_ticketing.presentation.concert.ConcertDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LoggingFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConcertFacade concertFacade;

    @Test
    public void 로깅_필터_테스트() throws Exception {
        // Mock 콘서트 데이터 설정
        List<ConcertDto> mockConcertDtos = List.of(
                new ConcertDto(1L, "아이유 콘서트"),
                new ConcertDto(2L, "비투비 콘서트")
        );

        // ConcertFacade의 getConcertDtos() 메서드가 mockConcerts를 반환하도록 설정
        when(concertFacade.getConcerts()).thenReturn(mockConcertDtos);

        // API 호출 및 검증
        mockMvc.perform(get("/api/concert/get-concerts"))
                .andExpect(status().isOk());
    }

}

