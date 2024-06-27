package com.hhplus.lecture.controller.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@NoArgsConstructor
@Getter
@Setter
public class LectureScheduleResponse {
    private Long id;

    private Long lectureId;

    private Timestamp lectureDate;

    private Integer maxPersonnel = 30;

    private Integer currentPersonnel;
}
