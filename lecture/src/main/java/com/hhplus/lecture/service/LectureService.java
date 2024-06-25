package com.hhplus.lecture.service;

import com.hhplus.lecture.dto.LectureRequest;
import com.hhplus.lecture.dto.LectureResponse;
import com.hhplus.lecture.entity.Lecture;
import com.hhplus.lecture.repository.LectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
public class LectureService {
    private final LectureRepository lectureRepository;

    public void signUpLecture(LectureRequest request) {
        LectureResponse response = getLectureInfo(request.getId());



    }

    public LectureResponse getLectureInfo(Long id) {
        Lecture response = lectureRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 특강이 존재하지 않습니다."));
        return new LectureResponse(response);
    }
}
