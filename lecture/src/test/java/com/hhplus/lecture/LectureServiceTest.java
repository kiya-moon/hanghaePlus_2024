package com.hhplus.lecture;

import com.hhplus.lecture.controller.dto.AppliedLectureResponse;
import com.hhplus.lecture.controller.dto.LectureListResponse;
import com.hhplus.lecture.controller.dto.LectureRequest;
import com.hhplus.lecture.infra.entity.LectureHistory;
import com.hhplus.lecture.infra.entity.LectureSchedule;
import com.hhplus.lecture.domain.repository.LectureHistoryRepository;
import com.hhplus.lecture.domain.repository.LectureScheduleRepository;
import com.hhplus.lecture.domain.service.LectureService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LectureServiceTest {

    @Mock
    private LectureScheduleRepository lectureScheduleRepository;

    @Mock
    private LectureHistoryRepository lectureHistoryRepository;

    @InjectMocks
    private LectureService lectureService;

    @Test
    @DisplayName("특강 신청 - 특강이 존재하지 않는 경우")
    void testApplyLecture_LectureNotFound() {
        // Given
        LectureRequest request = new LectureRequest();
        request.setUserId(1L);
        request.setLectureScheduleId(1L);

        when(lectureScheduleRepository.findByIdWithLock(request.getLectureScheduleId()))
                .thenReturn(Optional.empty());

        // When
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            lectureService.applyLecture(request);
        });

        // Then
        assertEquals("해당 특강이 존재하지 않습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("특강 신청 - 이미 신청한 특강인 경우")
    void testApplyLecture_AlreadyApplied() {
        // Given
        LectureRequest request = new LectureRequest();
        request.setUserId(1L);
        request.setLectureScheduleId(1L);
        LectureSchedule lectureSchedule = LectureSchedule.builder().build();

        when(lectureScheduleRepository.findByIdWithLock(request.getLectureScheduleId()))
                .thenReturn(Optional.of(lectureSchedule));
        when(lectureHistoryRepository.findByUserIdAndLectureScheduleId(request.getUserId(), request.getLectureScheduleId()))
                .thenReturn(Optional.of(LectureHistory.builder().build()));

        // When
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            lectureService.applyLecture(request);
        });

        // Then
        assertEquals("이미 신청한 특강입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("특강 신청 - 성공")
    void testApplyLecture_SUCCESS() {
        // Given
        LectureRequest request = new LectureRequest();
        request.setUserId(1L);
        request.setLectureScheduleId(1L);
        LectureSchedule lectureSchedule = LectureSchedule.builder()
                .currentPersonnel(5)
                .maxPersonnel(30)
                .build();

        when(lectureScheduleRepository.findByIdWithLock(request.getLectureScheduleId()))
                .thenReturn(Optional.of(lectureSchedule));
        when(lectureHistoryRepository.findByUserIdAndLectureScheduleId(request.getUserId(), request.getLectureScheduleId()))
                .thenReturn(Optional.empty());
        when(lectureHistoryRepository.countByLectureScheduleId(request.getLectureScheduleId()))
                .thenReturn(5);

        // When
        ResponseEntity<String> response = lectureService.applyLecture(request);

        // Then
        assertEquals("특강 신청이 완료되었습니다.", response.getBody());
    }

    @Test
    @DisplayName("특강 신청 - 인원 마감")
    void testApplyLecture_FullCapacity() {
        // Given
        LectureRequest request = new LectureRequest();
        request.setUserId(1L);
        request.setLectureScheduleId(1L);
        LectureSchedule lectureSchedule = LectureSchedule.builder()
                .currentPersonnel(30)
                .maxPersonnel(30)
                .build();

        when(lectureScheduleRepository.findByIdWithLock(request.getLectureScheduleId()))
                .thenReturn(Optional.of(lectureSchedule));
        when(lectureHistoryRepository.findByUserIdAndLectureScheduleId(request.getUserId(), request.getLectureScheduleId()))
                .thenReturn(Optional.empty());
        when(lectureHistoryRepository.countByLectureScheduleId(request.getLectureScheduleId()))
                .thenReturn(30);

        // When
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            lectureService.applyLecture(request);
        });

        // Then
        assertEquals("특강 신청이 마감되었습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("특강 신청 - 실패")
    void testApplyLecture_FAIL() {
        // Given
        LectureRequest request = new LectureRequest();
        request.setUserId(1L);
        request.setLectureScheduleId(1L);
        LectureSchedule lectureSchedule = LectureSchedule.builder()
                .currentPersonnel(5)
                .maxPersonnel(30)
                .build();

        when(lectureScheduleRepository.findByIdWithLock(request.getLectureScheduleId()))
                .thenReturn(Optional.of(lectureSchedule));
        when(lectureHistoryRepository.findByUserIdAndLectureScheduleId(request.getUserId(), request.getLectureScheduleId()))
                .thenReturn(Optional.empty());
        when(lectureHistoryRepository.countByLectureScheduleId(request.getLectureScheduleId()))
                .thenReturn(5);
        doThrow(new RuntimeException()).when(lectureHistoryRepository).save(any(LectureHistory.class));

        // When
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            lectureService.applyLecture(request);
        });

        // Then
        assertEquals("특강 신청에 실패했습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("특강 목록 조회")
    void testGetLectureList() {
        // Given
        LectureSchedule lecture1 = LectureSchedule.builder()
                .id(1L)
                .lectureId(101L)
                .lectureDate(new Timestamp(System.currentTimeMillis()))
                .maxPersonnel(30)
                .currentPersonnel(10)
                .build();

        LectureSchedule lecture2 = LectureSchedule.builder()
                .id(2L)
                .lectureId(102L)
                .lectureDate(new Timestamp(System.currentTimeMillis() + 3600000))
                .maxPersonnel(30)
                .currentPersonnel(15)
                .build();

        when(lectureScheduleRepository.findAll()).thenReturn(Arrays.asList(lecture1, lecture2));

        // When
        List<LectureListResponse> lectureList = lectureService.getLectureList();

        // Then
        assertEquals(2, lectureList.size());

        LectureListResponse response1 = lectureList.get(0);
        assertEquals(1L, response1.getId());

        LectureListResponse response2 = lectureList.get(1);
        assertEquals(2L, response2.getId());
    }

    @Test
    @DisplayName("특강 신청 성공 여부 조회 - 성공")
    void testIsAppliedLecture_SUCCESS() {
        // Given
        Long userId = 1L;
        Long lectureScheduleId = 1L;
        LectureHistory lectureHistory = LectureHistory.builder()
                .id(1L)
                .lectureScheduleId(lectureScheduleId)
                .userId(userId)
                .registerDate(new Timestamp(System.currentTimeMillis()))
                .build();

        when(lectureHistoryRepository.findByUserIdAndLectureScheduleId(userId, lectureScheduleId))
                .thenReturn(Optional.of(lectureHistory));

        // When
        AppliedLectureResponse response = lectureService.isAppliedLecture(userId, lectureScheduleId);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    @DisplayName("특강 신청 성공 여부 조회 - 실패")
    void testIsAppliedLecture_FAIL() {
        // Given
        Long userId = 1L;
        Long lectureScheduleId = 1L;

        when(lectureHistoryRepository.findByUserIdAndLectureScheduleId(userId, lectureScheduleId))
                .thenReturn(Optional.empty());

        // When
        AppliedLectureResponse response = lectureService.isAppliedLecture(userId, lectureScheduleId);

        // Then
        assertNull(response);
    }
}

