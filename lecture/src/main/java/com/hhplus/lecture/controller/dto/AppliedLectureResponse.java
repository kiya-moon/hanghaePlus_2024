package com.hhplus.lecture.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AppliedLectureResponse {
    private Long id;
    private Long lectureScheduleId;
    private Long userId;
    Timestamp registerDate;
}
