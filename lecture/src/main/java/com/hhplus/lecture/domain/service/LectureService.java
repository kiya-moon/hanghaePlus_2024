package com.hhplus.lecture.domain.service;

import com.hhplus.lecture.controller.dto.*;
import com.hhplus.lecture.infra.entity.LectureHistory;
import com.hhplus.lecture.infra.entity.LectureSchedule;
import com.hhplus.lecture.domain.repository.LectureHistoryRepository;
import com.hhplus.lecture.domain.repository.LectureScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LectureService {
    private final LectureScheduleRepository lectureScheduleRepository;
    private final LectureHistoryRepository lectureHistoryRepository;

    // 특강 신청 로직
    public ResponseEntity<String> applyLecture(LectureRequest request) {
        // 특강 조회
        LectureSchedule lectureSchedule = lectureScheduleRepository.findById(request.getLectureScheduleId())
                .orElseThrow(() -> new IllegalStateException("해당 특강이 존재하지 않습니다."));

        // 특강 신청 가능 여부 확인
        Optional<LectureHistory> existingApplication = lectureHistoryRepository.findByUserIdAndLectureScheduleId(request.getUserId(), request.getLectureScheduleId());
        if (existingApplication.isPresent()) {
            throw new IllegalStateException("이미 신청한 특강입니다.");
        }

        // 특강 히스토리 카운트 조회
        int countPersonnel = lectureHistoryRepository.countByLectureScheduleId(request.getLectureScheduleId());

        if((lectureSchedule.getCurrentPersonnel() < lectureSchedule.getMaxPersonnel())
                & (countPersonnel < lectureSchedule.getMaxPersonnel())) {
            // 인원 미달이면 특강 신청 가능
            LectureHistory lectureHistory = LectureHistory.builder()
                    .userId(request.getUserId())
                    .lectureScheduleId(request.getLectureScheduleId())
                    .registerDate(new Timestamp(System.currentTimeMillis()))
                    .build();
            try {
                lectureHistoryRepository.save(lectureHistory);
                // 현재 인원 업데이트
                lectureHistoryRepository.updateCurrentPersonnel(request.getLectureScheduleId(), countPersonnel + 1);
                return ResponseEntity.ok("특강 신청이 완료되었습니다.");
            } catch (Exception e) {
                throw new IllegalStateException("특강 신청에 실패했습니다.");
            }
        } else {
            throw new IllegalStateException("특강 신청이 마감되었습니다.");
        }
    }

    // 특강 목록 조회 로직
    public List<LectureListResponse> getLectureList() {

        return lectureScheduleRepository.findAll().stream()
                .map(this::convertToLectureListResponse)
                .collect(Collectors.toList());
    }

    // LectureSchedule 객체를 LectureListResponse 객체로 변환
    private LectureListResponse convertToLectureListResponse(LectureSchedule lectureSchedule) {
        return new LectureListResponse(
                lectureSchedule.getId(),
                lectureSchedule.getLectureId(),
                lectureSchedule.getLectureDate(),
                lectureSchedule.getMaxPersonnel(),
                lectureSchedule.getCurrentPersonnel()
        );
    }

    // 특강 신청 성공 여부 조회 로직
    public AppliedLectureResponse isAppliedLecture(Long userId, Long lectureScheduleId) {
        return lectureHistoryRepository.findByUserIdAndLectureScheduleId(userId, lectureScheduleId)
                .map(this::convertToAppliedLectureResponse)
                .orElse(null);
    }

    // LectureHistory 객체를 AppliedLectureResponse 객체로 변환
    private AppliedLectureResponse convertToAppliedLectureResponse(LectureHistory lectureHistory) {
        return new AppliedLectureResponse(
                lectureHistory.getId(),
                lectureHistory.getLectureScheduleId(),
                lectureHistory.getUserId(),
                lectureHistory.getRegisterDate()
        );
    }
}
