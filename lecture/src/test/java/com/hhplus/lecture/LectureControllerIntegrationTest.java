package com.hhplus.lecture;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhplus.lecture.controller.dto.AppliedLectureResponse;
import com.hhplus.lecture.controller.dto.LectureListResponse;
import com.hhplus.lecture.controller.dto.LectureRequest;
import com.hhplus.lecture.domain.repository.LectureHistoryRepository;
import com.hhplus.lecture.domain.repository.LectureScheduleRepository;
import com.hhplus.lecture.infra.entity.LectureHistory;
import com.hhplus.lecture.infra.entity.LectureSchedule;
import com.hhplus.lecture.domain.service.LectureService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LectureControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LectureService lectureService;

    @MockBean
    private LectureScheduleRepository lectureScheduleRepository;

    @MockBean
    private LectureHistoryRepository lectureHistoryRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        // Mock 데이터 설정
        List<LectureListResponse> mockLectureList = new ArrayList<>();
        mockLectureList.add(new LectureListResponse(1L, 1L, new Timestamp(System.currentTimeMillis()), 30, 15));
        mockLectureList.add(new LectureListResponse(2L, 2L, new Timestamp(System.currentTimeMillis()), 30, 10));

        AppliedLectureResponse mockAppliedResponse = new AppliedLectureResponse(1L, 1L, 1L, new Timestamp(System.currentTimeMillis()));

        LectureSchedule mockLectureSchedule = new LectureSchedule(1L, 1L, new Timestamp(System.currentTimeMillis()), 30, 15);

        LectureHistory mockLectureHistory = new LectureHistory(1L, 1L, 1L, new Timestamp(System.currentTimeMillis()));

        // 특강 목록 조회 Mock 설정
        when(lectureService.getLectureList()).thenReturn(mockLectureList);

        // 특강 신청 Mock 설정
        when(lectureService.applyLecture(any(LectureRequest.class))).thenReturn(ResponseEntity.ok("특강 신청이 완료되었습니다."));

        // 특강 신청 성공 여부 조회 Mock 설정
        when(lectureService.isAppliedLecture(anyLong(), anyLong())).thenReturn(mockAppliedResponse);

        // Mock Repository 설정
        when(lectureScheduleRepository.findById(anyLong())).thenReturn(Optional.of(mockLectureSchedule));
        when(lectureHistoryRepository.findByUserIdAndLectureScheduleId(anyLong(), anyLong())).thenReturn(Optional.of(mockLectureHistory));
    }

    @Test
    public void testGetLectureList() throws Exception {
        mockMvc.perform(get("/lectures/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].lectureId").value(1))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].lectureId").value(2));
    }

    @Test
    public void testApplyLecture() throws Exception {
        LectureRequest request = new LectureRequest(1L, 1L);
        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/lectures/apply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(content().string("특강 신청이 완료되었습니다."));
    }

    @Test
    public void testIsAppliedLecture() throws Exception {
        mockMvc.perform(get("/lectures/application/1/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.lectureScheduleId").value(1))
                .andExpect(jsonPath("$.userId").value(1));
    }
}

