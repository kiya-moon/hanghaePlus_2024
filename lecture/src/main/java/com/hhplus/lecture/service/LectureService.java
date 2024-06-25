package com.hhplus.lecture.service;

import com.hhplus.lecture.repository.LectureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LectureService {
    @Autowired
    LectureRepository lectureRepository;
}
