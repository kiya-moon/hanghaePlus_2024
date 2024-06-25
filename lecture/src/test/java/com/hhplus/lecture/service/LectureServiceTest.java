package com.hhplus.lecture.service;

import com.hhplus.lecture.dto.LectureResponse;
import com.hhplus.lecture.entity.Lecture;
import com.hhplus.lecture.repository.LectureRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LectureServiceTest {
    @InjectMocks
    private LectureService lectureService;

    @Mock
    private LectureRepository lectureRepository;

    // 특강 정보 조회
    @Test
    @DisplayName("특강 정보 조회 테스트")
    void getLectureInfo() {
        // given
        Long lectureId = 1L;
        Lecture mockLecture = Lecture.builder().
                id(lectureId).
                lectureNm("test").
                applicationDate(null).
                lectureCapacity("30").
                build();
        Mockito.when(lectureRepository.findById(lectureId)).thenReturn(Optional.of(mockLecture));

        // when
        LectureResponse response = lectureService.getLectureInfo(lectureId);

        // then
        assertNotNull(response);
    }

    @Test
    @DisplayName("특강 정보 조회 실패 테스트")
    void getLectureInfoFailure() {
        // given
        Long lectureId = 1L;
        Mockito.when(lectureRepository.findById(lectureId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            lectureService.getLectureInfo(lectureId);
        });
    }
}