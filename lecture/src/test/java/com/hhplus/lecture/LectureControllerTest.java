package com.hhplus.lecture;

import com.hhplus.lecture.controller.LectureController;
import com.hhplus.lecture.controller.dto.AppliedLectureResponse;
import com.hhplus.lecture.controller.dto.LectureListResponse;
import com.hhplus.lecture.controller.dto.LectureRequest;
import com.hhplus.lecture.controller.dto.LectureResponse;
import com.hhplus.lecture.domain.service.LectureService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class LectureControllerTest {

    @Mock
    private LectureService lectureService;

    @InjectMocks
    private LectureController lectureController;

    @BeforeEach
    void setUp() {
        // Mockito 애노테이션을 초기화
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("특강 신청 성공 테스트")
    void testApplyLecture_SUCCESS() {
        // Given
        LectureRequest request = new LectureRequest();
        request.setUserId(1L);
        request.setLectureScheduleId(1L);

        // `applyLecture` 메서드가 성공 메시지와 함께 200 OK 상태를 반환하도록 설정
        when(lectureService.applyLecture(any(LectureRequest.class))).thenReturn(ResponseEntity.ok("특강 신청이 완료되었습니다."));

        // When
        ResponseEntity<String> response = lectureController.applyLecture(request);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("특강 신청이 완료되었습니다.", response.getBody());
    }

    @Test
    @DisplayName("특강 신청 실패 테스트")
    void testApplyLecture_FAIL() {
        // Given
        LectureRequest request = new LectureRequest();
        request.setUserId(1L);
        request.setLectureScheduleId(1L);

        // `applyLecture` 메서드가 `IllegalStateException`을 던지도록 설정
        when(lectureService.applyLecture(any(LectureRequest.class))).thenThrow(new IllegalStateException("특강 신청이 마감되었습니다."));

        // When
        // Then
        assertThrows(IllegalStateException.class, () -> lectureController.applyLecture(request));
    }

    @Test
    @DisplayName("특강 목록 조회 테스트")
    void testGetLectureList() {
        // Given
        List<LectureListResponse> mockResponse = new ArrayList<>();

        LectureListResponse lecture1 = new LectureListResponse();
        lecture1.setId(1L);
        lecture1.setLectureId(1L);
        lecture1.setLectureDate(Timestamp.valueOf("2024-07-01 10:00:00"));
        lecture1.setMaxPersonnel(30);
        lecture1.setCurrentPersonnel(10);

        LectureListResponse lecture2 = new LectureListResponse();
        lecture2.setId(2L);
        lecture2.setLectureId(1L);
        lecture2.setLectureDate(Timestamp.valueOf("2024-07-02 10:00:00"));
        lecture2.setMaxPersonnel(30);
        lecture2.setCurrentPersonnel(15);

        mockResponse.add(lecture1);
        mockResponse.add(lecture2);

        when(lectureService.getLectureList()).thenReturn(mockResponse);

        // When
        ResponseEntity<List<LectureListResponse>> response = lectureController.getLectureList();

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        List<LectureListResponse> lectures = response.getBody();
        assertEquals(2, lectures.size());
    }

    @Test
    @DisplayName("특강 신청 완료 여부 조회 테스트 - 성공")
    void testIsAppliedLecture_SUCCESS() {
        // Given
        long userId = 1L;
        long lectureScheduleId = 1L;

        AppliedLectureResponse mockResponse = new AppliedLectureResponse();
        mockResponse.setId(1L);
        mockResponse.setLectureScheduleId(1L);
        mockResponse.setUserId(1L);
        mockResponse.setRegisterDate(Timestamp.valueOf("2024-06-27 10:00:00"));

        when(lectureService.isAppliedLecture(userId, lectureScheduleId)).thenReturn(mockResponse);

        // When
        ResponseEntity<AppliedLectureResponse> response = lectureController.isAppliedLecture(userId, lectureScheduleId);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        AppliedLectureResponse appliedLectureResponse = response.getBody();
        assertEquals(1L, appliedLectureResponse.getId());
    }

    @Test
    @DisplayName("특강 신청 완료 여부 조회 테스트 - 실패")
    void testIsAppliedLecture_FAIL() {
        // Given
        long userId = 1L;
        long lectureScheduleId = 1L;

        when(lectureService.isAppliedLecture(userId, lectureScheduleId)).thenReturn(null);

        // When
        ResponseEntity<AppliedLectureResponse> response = lectureController.isAppliedLecture(userId, lectureScheduleId);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
}



