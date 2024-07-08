package com.hhplus.lecture.controller;

import com.hhplus.lecture.controller.dto.AppliedLectureResponse;
import com.hhplus.lecture.controller.dto.LectureListResponse;
import com.hhplus.lecture.controller.dto.LectureRequest;
import com.hhplus.lecture.controller.dto.LectureResponse;
import com.hhplus.lecture.domain.service.LectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lectures")
@RequiredArgsConstructor
public class LectureController {
    private final LectureService lectureService;

    // 특강 신청 API
    @PostMapping("/apply")
    public ResponseEntity<String> applyLecture(@RequestBody LectureRequest request) {
        return lectureService.applyLecture(request);
    }

    // 특강 목록 API
    @GetMapping("/")
    public ResponseEntity<List<LectureListResponse>> getLectureList() {
        List<LectureListResponse> response = lectureService.getLectureList();
        return ResponseEntity.ok(response);
    }

    // 특강 신청 성공 여부 조회 API
    @GetMapping("application/{userId}/{lectureScheduleId}")
    public ResponseEntity<AppliedLectureResponse> isAppliedLecture(@PathVariable Long userId, @PathVariable Long lectureScheduleId) {
        AppliedLectureResponse response = lectureService.isAppliedLecture(userId, lectureScheduleId);
        if (response != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
