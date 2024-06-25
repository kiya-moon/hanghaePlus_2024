package com.hhplus.lecture.controller;

import com.hhplus.lecture.dto.LectureRequest;
import com.hhplus.lecture.service.LectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/lecture")
@RequiredArgsConstructor
public class LectureController {
    private final LectureService lectureService;

    // 특강 신청 API
    @PostMapping("/apply")
    public ResponseEntity<String> signUpLecture(@RequestBody LectureRequest request) {

        lectureService.signUpLecture(request);
        return ResponseEntity.ok("success");
    }
}
