package com.hhplus.lecture.dto;

import com.hhplus.lecture.entity.Lecture;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class LectureResponse {
    private Long id;

    private String lectureNm;

    private Timestamp applicationDate;

    private String lectureCapacity;

    public LectureResponse(Lecture lecture) {
            this.id = lecture.getId();
            this.lectureNm = lecture.getLectureNm();
            this.applicationDate = lecture.getApplicationDate();
            this.lectureCapacity = lecture.getLectureCapacity();
    }
}
