package com.hhplus.lecture.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LectureListResponse {
    private Long id;

    private Long lectureId;

    private Timestamp lectureDate;

    private Integer maxPersonnel;

    private Integer currentPersonnel;

}
